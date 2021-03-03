package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain

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
        val dispatcher = TestCoroutineDispatcher()

        composeTestRule.setContent {
            LoadingDataThing(MyViewModel(dispatcher))
        }
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText("OK!")
            .assertExists()
    }
    @Test
    fun testCoroutineDispatcher_advanceTime_setMain() { // timeouts
        val dispatcher = TestCoroutineDispatcher()

        Dispatchers.setMain(dispatcher)
        composeTestRule.setContent {
            LoadingDataThing(MyViewModel(dispatcher))
        }
        dispatcher.advanceUntilIdle()

        composeTestRule.onNodeWithText("OK!")
            .assertExists()
    }

    @Test(expected = AssertionError::class)
    fun testCoroutineDispatcher_waitForIdle() {
        val dispatcher = TestCoroutineDispatcher()

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
