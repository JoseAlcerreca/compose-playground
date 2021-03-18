package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalStateException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


@RunWith(AndroidJUnit4::class)
class SingleLiveEventTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val busyText = "Working..."
    private val received_text = "Received effect"

    @Test
    fun receiveAsFlowTest() {
        val channel = Channel<Int>(capacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        runBlocking {
            channel.send(0)
            channel.send(1)
            val observer1 = channel.receiveAsFlow()
            val observer2 = channel.receiveAsFlow()
            channel.send(2)
            channel.close()

            //Buffer replayed to first observer
            assertEquals(observer1.toList(), listOf(1,2))
            // Second observer does not receive old nor new updates
            assertEquals(observer2.toList().count(), 0)
        }
    }

    @Test
    fun SharedFlowReplayTest() {
        val sharedFlow = MutableSharedFlow<Int>(replay = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

        runBlocking {
            sharedFlow.emit(0)
            sharedFlow.emit(1)
            sharedFlow.emit(2)
            val observer1 = sharedFlow.toList()
            sharedFlow.emit(3)
            val observer2 = sharedFlow.toList()

            assertEquals(observer1, listOf(1,2))
            assertEquals(observer2.count(), 0)
        }
    }
    /**
     * Make sure that effects that are set before the view starts collecting are buffered.
     */
    @Test
    fun effectSetBeforeView_isReceived() {
        val viewmodel = SingleEventViewModelSharedFlow().apply { addEffect() }

        val showViewState = mutableStateOf(false)
        composeTestRule.setContent {
            var showView by remember { showViewState }

            // Start collecting after a while
            LaunchedEffect(showView) {
                delay(200)
                viewmodel.effects.collect {
                    showView = true
                }
            }
            if (showView) {
                Text(received_text)
            } else {
                Text(busyText)
            }
        }
        // Before we start collecting
        composeTestRule.onNodeWithText(busyText).assertExists()

        // Wait until we start collecting
        composeTestRule.waitUntil { showViewState.value }

        // Assert the effect was received
        composeTestRule.onNodeWithText(received_text).assertExists()
    }

    @Test
    fun shareInTest() {
        val viewmodel = SingleEventViewModelShareIn().apply { addEffect() }

        composeTestRule.setContent {
            var counter1 by remember { mutableStateOf(0) }
            var counter2 by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                delay(200)
                viewmodel.effects.collect {
                    counter1 += 1
                }
            }

            // Start collecting after a while
            LaunchedEffect(Unit) {
                delay(400)
                viewmodel.effects.collect {
                    counter2 += 1
                }
            }
            Column {
                Text("Collector 1: $counter1")
                Text("Collector 2: $counter2")
            }
        }
        composeTestRule.mainClock.advanceTimeBy(400)
        // Initially, only the first collector receives the cached value
        composeTestRule.onNodeWithText("Collector 1: 1").assertExists()
        composeTestRule.onNodeWithText("Collector 2: 0").assertExists()
        // When a new effect is added:
        viewmodel.addEffect()
        // Both collectors receive the message
        composeTestRule.onNodeWithText("Collector 1: 2").assertExists()
        composeTestRule.onNodeWithText("Collector 2: 1").assertExists()

    }

    @Test(expected = IllegalStateException::class)
    fun twoViewsObserveSame() {
        val consumeAsFlowViewModel = SingleEventViewModelConsumeAsFlow()
        val scope = CoroutineScope(SupervisorJob())
        val handler = CoroutineExceptionHandler { context, exception ->
            // Assert exception
        }
        composeTestRule.setContent {
            // Show one view and then show another one after a while
            Column {
                ObserveSharedFlow(consumeAsFlowViewModel, scope, handler)
                ObserveSharedFlow(consumeAsFlowViewModel, scope, handler)
            }
        }
        assertFailsWith<IllegalStateException> {
            composeTestRule.waitForIdle()
        }
    }

    @Test(expected = IllegalStateException::class)
    fun twoViewsObserveSame_differentTimes() {
        val timer = mutableStateOf(0)
        val sharedFlowViewModel = SingleEventViewModelConsumeAsFlow()
        composeTestRule.setContent {
            var rememberTimer by remember { timer }
            // Delay one of the ways
            LaunchedEffect(Unit) {
                delay(100)
                rememberTimer += 1
            }
            // Show one view and then show another one after a while
            Column {
                ObserveSharedFlow(sharedFlowViewModel)
                if (rememberTimer > 0) {
                    ObserveSharedFlow(sharedFlowViewModel)
                }
            }
        }
        composeTestRule.waitUntil { timer.value == 2 }
    }

//    @Test
//    fun blah() {
//        val _effects = Channel<String>(
//            capacity = 3,
//            onBufferOverflow = BufferOverflow.DROP_OLDEST
//        )
//        // Allow multiple collectors, first one drains
//        val effects: Flow<String> = _effects
//            .receiveAsFlow()
//            .shareIn(scope, started = SharingStarted.WhileSubscribed())
//
//        val sharedFlow = MutableSharedFlow<SingleEventViewModelEffects>(
//            extraBufferCapacity = 2,
//            onBufferOverflow = BufferOverflow.DROP_OLDEST
//        )
//        val stateFlow = MutableStateFlow(SingleEventViewModelEffects.NavigateBack)
//
//        val shareIn = _effects.receiveAsFlow().shareIn(scope, SharingStarted.WhileSubscribed(), 0)
//    }
}

/**
 * Views
 */
@Composable
private fun ObserveSharedFlow(
    viewmodel: SingleEventViewModel,
    scope: CoroutineScope = rememberCoroutineScope(),
    handler: CoroutineContext = EmptyCoroutineContext
) {
    LaunchedEffect(key1 = viewmodel.effects) {
//        scope.launch(context = handler) {
            viewmodel.effects.collect {
//        }

        }
    }

}

/**
 * Allow multiple collectors, all of them get it, drop oldest if no observers.
 */
private class SingleEventViewModelSharedFlow : SingleEventViewModel() {
    // Allow multiple collectors
    private val _effects = MutableSharedFlow<SingleEventViewModelEffects>(
//        replay = 1,
        extraBufferCapacity = 2,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val effects: Flow<SingleEventViewModelEffects> = _effects

    fun addEffect() {
        viewModelScope.launch {
            _effects.emit(SingleEventViewModelEffects.NavigateBack)
        }
    }
}

/**
 * Allow only one collector, or throw.
 */
private class SingleEventViewModelConsumeAsFlow : SingleEventViewModel() {
    // Allow only one collector or crash
    private val _effects = Channel<SingleEventViewModelEffects>()
    override val effects: Flow<SingleEventViewModelEffects> = _effects.consumeAsFlow()
}

/**
 * Allows multiple collectors but only one gets it.
 */
private class SingleEventViewModelReceiveAsFlow : SingleEventViewModel() {
    private val _effects = Channel<SingleEventViewModelEffects>(
        capacity = 3,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    // Allow multiple collectors but only one receives data
    override val effects: Flow<SingleEventViewModelEffects> = _effects.receiveAsFlow()
}

/**
 * Allows multiple collectors but only one gets it.
 */
private class SingleEventViewModelShareIn : ViewModel() {
    private val _effects = Channel<String>(
        capacity = 3,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    // Allow multiple collectors, first one drains
    val effects: Flow<String> = _effects
        .receiveAsFlow()
        .shareIn(viewModelScope, started = SharingStarted.WhileSubscribed())

    fun addEffect() {
        viewModelScope.launch {
            _effects.send("Added manually")
        }
    }
}

private sealed class SingleEventViewModelEffects {
    object NavigateBack : SingleEventViewModelEffects()
    class NavigateToDestination1: SingleEventViewModelEffects()
    class NavigateToDestination2(val id: String): SingleEventViewModelEffects()
}
/**
Abstract ViewModel
 */
private abstract class SingleEventViewModel : ViewModel() {
    abstract val effects: Flow<SingleEventViewModelEffects>
}
