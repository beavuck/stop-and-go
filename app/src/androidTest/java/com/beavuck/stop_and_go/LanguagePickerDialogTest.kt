package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.dialogs.LanguagePickerDialog
import com.beavuck.stop_and_go.model.SupportedLocale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguagePickerDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun languagePickerDialog_displaysAllLocales() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        SupportedLocale.entries.forEach { locale ->
            composeTestRule.onNodeWithText(locale.getDisplayName(context))
                .performScrollTo()
                .assertIsDisplayed()
        }
    }

    @Test
    fun languagePickerDialog_highlightsCurrentLocale() {
        val currentLocale = SupportedLocale.FRENCH

        composeTestRule.setContent {
            LanguagePickerDialog(
                currentLocale = currentLocale,
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("locale_${currentLocale.code}")
            .performScrollTo()
            .assertIsSelected()
    }

    @Test
    fun languagePickerDialog_selectLocale_callsCallback() {
        var selectedLocaleCode: String? = null

        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = { code -> selectedLocaleCode = code },
                onDismiss = {}
            )
        }

        val targetLocale = SupportedLocale.SPANISH
        composeTestRule.onNodeWithTag("locale_${targetLocale.code}")
            .performScrollTo()
            .performClick()

        assert(selectedLocaleCode == targetLocale.code)
    }

    @Test
    fun languagePickerDialog_cancelButton_dismissesDialog() {
        var dismissCalled = false
        var localeSelected: String? = null

        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = { code -> localeSelected = code },
                onDismiss = { dismissCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("cancelButton")
            .performClick()

        assert(dismissCalled)
        assert(localeSelected == null)
    }

    @Test
    fun languagePickerDialog_displaysTitle() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText(context.getString(R.string.language))
            .assertIsDisplayed()
    }
}
