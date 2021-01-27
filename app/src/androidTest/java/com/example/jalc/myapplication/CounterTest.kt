package com.example.jalc.myapplication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CounterTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun counter_initially_zero() {
        // Given a counter in its initial state, assert that it has zero clicks
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.clicks_count, 0)).assertExists()
    }

    @Test
    fun clickButton_incrementsCounter() {
        // Click on the button to increment the number of clicks
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.bt_increment_counter)).performClick()

        // Check that the counter was incremented
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.clicks_count, 1)).assertExists()
    }
}