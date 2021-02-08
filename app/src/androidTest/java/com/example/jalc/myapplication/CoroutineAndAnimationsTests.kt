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

@RunWith(AndroidJUnit4::class)
class CoroutineAndAnimationsTests {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun counterTest() {
        composeTestRule.mainClock.autoAdvance = false // Test timeouts otherwise
        composeTestRule.setContent {
            ScrollBoxesSmoothAnimations()
        }
        composeTestRule.onNodeWithText("Item 1")
            .assertExists()
            .assertTopPositionInRootIsEqualTo(24.dp)
    }

    @Test
    fun loadingThing() {
        composeTestRule.mainClock.autoAdvance = false // Test timeouts otherwise
        composeTestRule.setContent {
            LoadingThing()
        }
        composeTestRule.mainClock.advanceTimeBy(1000)
        composeTestRule.onNodeWithText("Ready!")
            .assertExists()
    }
}

@Composable
fun ScrollBoxesSmoothAnimations() {

    // Smoothly scroll 100px on first composition
    val state = rememberScrollState()
    LaunchedEffect(Unit) { state.smoothScrollTo(100f) }

    // Move the list up and down indefinitely
    val transition = rememberInfiniteTransition()
    val y = transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(modifier = Modifier
        .background(Color.LightGray)
        .offset { IntOffset(0, y.value.roundToInt()) }
        .size(100.dp)
        .padding(horizontal = 8.dp)
        .verticalScroll(state)
    ) {
        repeat(10) {
            Text("Item $it", modifier = Modifier.padding(2.dp))
        }
    }
}

@Composable
fun LoadingThing() {
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