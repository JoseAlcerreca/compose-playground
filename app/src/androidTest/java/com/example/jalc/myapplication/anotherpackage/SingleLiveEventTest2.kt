package com.example.jalc.myapplication.anotherpackage

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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalStateException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertFailsWith


@RunWith(AndroidJUnit4::class)
class SingleLiveEventTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val busyText = "Working..."
    private val received_text = "Received effect"

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
 * Allow only one collector, or throw.
 */
private class SingleEventViewModelConsumeAsFlow : SingleEventViewModel() {
    // Allow only one collector
    private val _effects = Channel<SingleEventViewModelEffects>()
    override val effects: Flow<SingleEventViewModelEffects> = _effects.consumeAsFlow()
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
