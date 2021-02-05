package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoadingDataThingTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingDataThing() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            LoadingDataThing()
        }

        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.onNodeWithText("OK!")
            .assertExists() // This fails, why?
    }

    @Ignore("No way to use own TestCoroutineScope")
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadingDataThing2() {
        val scope = TestCoroutineScope()
        val newRecomposer = Recomposer(scope.coroutineContext)
        composeTestRule.runOnUiThread {
            composeTestRule.activity.setContent(newRecomposer) {
                LoadingDataThing()
            }
        }
        scope.advanceTimeBy(1000)
        scope.advanceUntilIdle()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Loading")
            .assertDoesNotExist() // Fails, but shouldn't

        composeTestRule.onNodeWithText("OK!")
            .assertExists() // Fails, but shouldn't
    }

    @Test
    fun run() {
        composeTestRule.setContent {
            LoadingDataThing()
        }

        composeTestRule.onNodeWithText("Loading")
            .assertExists() // SHOULD FAIL! But doesn't

        composeTestRule.mainClock.advanceTimeBy(1000)

        composeTestRule.onNodeWithText("OK!")
            .assertExists()
    }
}

@Preview
@Composable
fun Preview() {
    LoadingDataThing()
}
