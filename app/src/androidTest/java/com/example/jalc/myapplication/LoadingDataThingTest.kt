package com.example.jalc.myapplication

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

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