package com.example.jalc.myapplication

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createAndroidComposeRuleLegacy
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jalc.myapplication.ui.theme.MyApplicationTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule


@RunWith(AndroidJUnit4::class)
class CounterTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRuleLegacy<MainActivity>()

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