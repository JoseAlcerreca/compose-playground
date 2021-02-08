package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.yield

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineAndDataTests {

    // Use ComponentActivity because we want to call setContent and inject a dispatcher into the VM
    // but in a real app, you could use DI.
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    // FAILS!
    @Test(expected = AssertionError::class)
    fun testCoroutineDispatcher_runBlockingTest_doesNotProgressDelay() {
        val dispatcher = TestCoroutineDispatcher()
        dispatcher.runBlockingTest {
            composeTestRule.setContent {
                // This can be done with DI in a real app.
                LoadingDataThing(MyViewModel(dispatcher))
            }
            composeTestRule.mainClock.advanceTimeByFrame()
            // runBlockingTest only works if you call the suspend functions directly from its body.
            // So this assertion fails:
            composeTestRule.onNodeWithText("OK!")
                .assertExists()
        }
    }

    @Test
    fun testCoroutineDispatcher_advanceTime() {
        val dispatcher = TestCoroutineDispatcher() // .apply { resumeDispatcher() }

        composeTestRule.setContent {
            LoadingDataThing(MyViewModel(dispatcher))
        }
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText("OK!")
            .assertExists()
    }

    @Test(expected = AssertionError::class)
    fun testCoroutineDispatcher_waitForIdle() {
        val dispatcher = TestCoroutineDispatcher() // .apply { resumeDispatcher() }

        composeTestRule.setContent {
            LoadingDataThing(MyViewModel(dispatcher))
        }
        composeTestRule.waitForIdle() // Does not progress past delay

        composeTestRule.onNodeWithText("OK!") // FAILS
            .assertExists()
    }

    @Test
    fun testCoroutineDispatcher_pauseAndResume() {
        val dispatcher = TestCoroutineDispatcher().apply { pauseDispatcher() }
        composeTestRule.setContent {
            LoadingDataThing(MyViewModel(dispatcher))
        }

        // Since the dispatcher is paused, the text won't be updated
        composeTestRule.onNodeWithText("Loading")
            .assertExists()
        // ... until the dispatcher is resumed.
        dispatcher.resumeDispatcher()
        composeTestRule.onNodeWithText("OK!")
            .assertExists()
    }
}


@Composable
fun LoadingDataThing() {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1000)
        isLoading = false
    }
    val transition = rememberInfiniteTransition()
    val y = transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (isLoading) {
        Text("Loading")
    } else {
        Text("Ready!",
            modifier = Modifier.offset { IntOffset(0, y.value.roundToInt()) }
        )
    }
}