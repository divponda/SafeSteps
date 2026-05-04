package com.safesteps.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerScreenTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun timerScreenDisplaysStartButton() {
        // Exercise: navigate to Timer tab
        composeTestRule.onNodeWithText(context.getString(R.string.nav_timer)).performClick()

        // Verify: start button visible
        composeTestRule
            .onNodeWithText(context.getString(R.string.btn_start_timer))
            .assertIsDisplayed()
    }

    @Test
    fun startingTimerShowsStopButton() {
        // Exercise: navigate to Timer and start timer
        composeTestRule.onNodeWithText(context.getString(R.string.nav_timer)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.btn_start_timer)).performClick()

        // Verify: cancel button is now visible
        composeTestRule
            .onNodeWithText(context.getString(R.string.btn_stop_timer))
            .assertIsDisplayed()
    }

    @Test
    fun stoppingTimerRestoresStartButton() {
        // Exercise: start then cancel  timer
        composeTestRule.onNodeWithText(context.getString(R.string.nav_timer)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.btn_start_timer)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.btn_stop_timer)).performClick()

        // Verify: start button back
        composeTestRule
            .onNodeWithText(context.getString(R.string.btn_start_timer))
            .assertIsDisplayed()
    }
}
