package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.dialogs.LanguagePickerDialog
import com.beavuck.stop_and_go.config.SupportedLocale
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguagePickerDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val englishName by lazy { SupportedLocale.ENGLISH.getDisplayName(context) }
    private val frenchName by lazy { SupportedLocale.FRENCH.getDisplayName(context) }
    private val japaneseName by lazy { SupportedLocale.JAPANESE.getDisplayName(context) }
    private val arabicName by lazy { SupportedLocale.ARABIC.getDisplayName(context) }
    private val portugueseName by lazy { SupportedLocale.PORTUGUESE.getDisplayName(context) }

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
    fun languagePickerDialog_displaysSearchField() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_searchByLanguageName_filtersResults() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("FrAn")

        composeTestRule.onNodeWithText(frenchName)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(englishName)
            .assertDoesNotExist()
    }

    @Test
    fun languagePickerDialog_searchByLanguageCode_filtersResults() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput(SupportedLocale.FRENCH.code)

        composeTestRule.onNodeWithText(frenchName)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(englishName)
            .assertDoesNotExist()
    }

    @Test
    fun languagePickerDialog_searchIsCaseInsensitive() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput(englishName.lowercase())

        composeTestRule.onNodeWithText(englishName)
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_clearSearchButton_appearsWhenTyping() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("clearSearchButton")
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("test")

        composeTestRule.onNodeWithTag("clearSearchButton")
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_clearSearchButton_resetsSearch() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("fra")

        composeTestRule.onNodeWithText(frenchName)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(englishName)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("clearSearchButton")
            .performClick()

        composeTestRule.onNodeWithText(englishName)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(frenchName)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_searchWithNoMatches_showsNoLanguagesFoundMessage() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("xyz123notfound")

        composeTestRule.onNodeWithText(context.getString(R.string.no_languages_found))
            .assertIsDisplayed()

        SupportedLocale.entries.forEach { locale ->
            composeTestRule.onNodeWithTag("locale_${locale.code}")
                .assertDoesNotExist()
        }
    }

    @Test
    fun languagePickerDialog_searchAndSelectLocale_worksCorrectly() {
        var selectedLocaleCode: String? = null

        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = { code -> selectedLocaleCode = code },
                onDismiss = {}
            )
        }

        val targetLocale = SupportedLocale.JAPANESE

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput(japaneseName)

        composeTestRule.onNodeWithTag("locale_${targetLocale.code}")
            .performClick()

        assert(selectedLocaleCode == targetLocale.code)
    }

    @Test
    fun languagePickerDialog_partialSearch_findsMatches() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("port")

        composeTestRule.onNodeWithText(portugueseName)
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_searchJapanese_byName() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("日本")

        composeTestRule.onNodeWithText(japaneseName)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(englishName)
            .assertDoesNotExist()
    }

    @Test
    fun languagePickerDialog_searchJapanese_byCode() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput(SupportedLocale.JAPANESE.code)

        composeTestRule.onNodeWithText(japaneseName)
            .assertIsDisplayed()
    }

    @Test
    fun languagePickerDialog_searchArabic_byName() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput("العر")

        composeTestRule.onNodeWithText(arabicName)
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(englishName)
            .assertDoesNotExist()
    }

    @Test
    fun languagePickerDialog_searchArabic_byCode() {
        composeTestRule.setContent {
            LanguagePickerDialog(
                onLocaleSelected = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("languageSearchField")
            .performTextInput(SupportedLocale.ARABIC.code)

        composeTestRule.onNodeWithText(arabicName)
            .assertIsDisplayed()
    }
}
