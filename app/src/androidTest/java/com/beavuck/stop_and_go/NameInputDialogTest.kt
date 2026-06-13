package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.dialogs.NameInputDialog
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NameInputDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun nameInputDialog_showsInitialValue() {
        composeTestRule.setContent {
            NameInputDialog(
                title = "Rename",
                initialValue = "My Config",
                onConfirm = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("nameInputField")
            .assertIsDisplayed()
            .assertTextContains("My Config")
    }

    @Test
    fun nameInputDialog_confirmButton_callsOnConfirmWithText() {
        var confirmed: String? = null

        composeTestRule.setContent {
            NameInputDialog(
                title = "New Config",
                initialValue = "",
                onConfirm = { confirmed = it },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("nameInputField")
            .performTextInput("HIIT")
        composeTestRule.onNodeWithTag("confirmButton")
            .performClick()

        assertEquals("HIIT", confirmed)
    }

    @Test
    fun nameInputDialog_cancelButton_callsOnDismiss() {
        var dismissed = false

        composeTestRule.setContent {
            NameInputDialog(
                title = "New Config",
                initialValue = "",
                onConfirm = {},
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag("cancelButton")
            .performClick()

        assert(dismissed)
    }

    @Test
    fun nameInputDialog_editInitialValue_confirmsNewText() {
        var confirmed: String? = null

        composeTestRule.setContent {
            NameInputDialog(
                title = "Rename",
                initialValue = "Old Name",
                onConfirm = { confirmed = it },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("nameInputField")
            .performTextClearance()
        composeTestRule.onNodeWithTag("nameInputField")
            .performTextInput("New Name")
        composeTestRule.onNodeWithTag("confirmButton")
            .performClick()

        assertEquals("New Name", confirmed)
    }
}
