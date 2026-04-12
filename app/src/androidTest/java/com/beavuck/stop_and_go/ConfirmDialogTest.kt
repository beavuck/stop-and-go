package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.dialogs.ConfirmDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfirmDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun confirmDialog_showsTitleAndMessage() {
        composeTestRule.setContent {
            ConfirmDialog(
                title = "Delete Config",
                message = "Are you sure you want to delete \"HIIT\"?",
                onConfirm = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("Delete Config").assertIsDisplayed()
        composeTestRule.onNodeWithText("Are you sure you want to delete \"HIIT\"?").assertIsDisplayed()
    }

    @Test
    fun confirmDialog_confirmButton_callsOnConfirm() {
        var confirmed = false

        composeTestRule.setContent {
            ConfirmDialog(
                title = "Delete",
                message = "Sure?",
                onConfirm = { confirmed = true },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("confirmButton").performClick()

        assert(confirmed)
    }

    @Test
    fun confirmDialog_cancelButton_callsOnDismiss() {
        var dismissed = false

        composeTestRule.setContent {
            ConfirmDialog(
                title = "Delete",
                message = "Sure?",
                onConfirm = {},
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag("cancelButton").performClick()

        assert(dismissed)
    }
}
