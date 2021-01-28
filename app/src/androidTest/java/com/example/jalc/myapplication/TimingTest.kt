package com.example.jalc.myapplication

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimingTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun counterTest() {
        val myCounter = mutableStateOf(0)
        var lastSeenValue = 0
        composeTestRule.setContent {
            Text(myCounter.value.toString())
            lastSeenValue = myCounter.value
        }
        myCounter.value = 2
        assert(lastSeenValue == 2) // Fails
        composeTestRule.onNodeWithText("2").assertExists() // Passes
        // Fails -> assert(myCounter.value == 1)
//        // Passes
//        composeTestRule.waitUntil { myCounter.value == 1 }
//        // Or:
//        composeTestRule.runOnIdle {
//            assert(myCounter.value == 1)
//        }
    }
}