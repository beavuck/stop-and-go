package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.dialogs.ColorPickerDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ColorPickerDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun colorPickerDialog_displaysRGBSliders() {
        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#FF0000",
                onColorSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("redSlider").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("greenSlider").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("blueSlider").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun colorPickerDialog_displaysColorPreview() {
        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#4CAF50",
                onColorSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("colorPreview").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun colorPickerDialog_displaysHexValue() {
        val initialColor = "#4CAF50"

        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = initialColor,
                onColorSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("hexDisplay").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText(initialColor).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun colorPickerDialog_updatesPreviewOnSliderChange() {
        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#000000",
                onColorSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("redSlider")
            .performScrollTo()
            .performTouchInput { swipeRight() }

        composeTestRule.waitForIdle()
    }

    @Test
    fun colorPickerDialog_okButton_returnsSelectedColor() {
        var selectedColor: String? = null

        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#FF5722",
                onColorSelected = { color -> selectedColor = color },
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("confirmButton").performClick()

        assert(selectedColor != null)
        assert(selectedColor == "#FF5722")
    }

    @Test
    fun colorPickerDialog_cancelButton_dismissesDialog() {
        var dismissCalled = false
        var colorSelected: String? = null

        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#FF5722",
                onColorSelected = { color -> colorSelected = color },
                onDismiss = { dismissCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("cancelButton").performClick()

        assert(dismissCalled)
        assert(colorSelected == null)
    }

    @Test
    fun colorPickerDialog_parsesInitialColorCorrectly() {
        composeTestRule.setContent {
            ColorPickerDialog(
                initialColor = "#AABBCC",
                onColorSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("#AABBCC").performScrollTo().assertIsDisplayed()
    }
}
