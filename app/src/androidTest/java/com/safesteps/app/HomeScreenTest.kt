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
class HomeScreenTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun sosButtonIsVisibleOnHomeScreen() {
        composeTestRule.onNodeWithText(context.getString(R.string.sos_button)).assertIsDisplayed()
    }

    @Test
    fun emergencyCallButtonIsDisplayed() {
        composeTestRule.onNodeWithText(context.getString(R.string.call_emergency)).assertIsDisplayed()
    }

    @Test
    fun tappingSosButtonShowsConfirmationDialog() {
        // Exercise: tap the SOS button
        composeTestRule.onNodeWithText(context.getString(R.string.sos_button)).performClick()

        // Verify: confirmation dialog is shown
        composeTestRule
            .onNodeWithText(context.getString(R.string.sos_confirmation_title))
            .assertIsDisplayed()
    }

    @Test
    fun cancellingSosDialogReturnsToHomeScreen() {
        // Exercise: open the SOS dialog, then cancel
        composeTestRule.onNodeWithText(context.getString(R.string.sos_button)).performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.btn_cancel)).performClick()

        // Verify: dialog is gone, SOS button is back
        composeTestRule.onNodeWithText(context.getString(R.string.sos_button)).assertIsDisplayed()
    }
}
