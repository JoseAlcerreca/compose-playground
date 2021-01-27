package com.example.jalc.myapplication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class InteropTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainInteropActivity>()

    @Test
    fun androidView_InteropTest() {
        // Check the initial state of a TextView that depends on a Compose state:
        Espresso.onView(withText("Hello Views")).check(matches(isDisplayed()))
        // Click on the Compose button that changes the state
        composeTestRule.onNodeWithText("Click here").performClick()
        // Check the new value
        Espresso.onView(withText("Hello Compose")).check(matches(isDisplayed()))
    }
}