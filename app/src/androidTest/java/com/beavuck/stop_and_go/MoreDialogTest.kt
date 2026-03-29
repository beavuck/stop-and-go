package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.dialogs.MoreDialog
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoreDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var stateRepository: StateRepository
    private lateinit var configRepository: ConfigRepository

    @Before
    fun setup() {
        stateRepository = StateRepository(context)
        configRepository = ConfigRepository(context)
    }

    @After
    fun tearDown() {
        stateRepository.clearState()
        configRepository.clearConfig()
    }

    @Test
    fun moreDialog_displaysAllLinkTiles() {
        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("moreAboutTile")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("morePrivacyTile")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("moreLicenseTile")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("moreTipTile")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun moreDialog_displaysShareButton() {
        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("moreShareButton")
            .assertIsDisplayed()
    }

    @Test
    fun moreDialog_displaysRateButton() {
        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("moreRateButton")
            .assertIsDisplayed()
    }

    @Test
    fun moreDialog_displaysResetButton() {
        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("moreResetButton")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun moreDialog_okButton_dismissesDialog() {
        var dismissCalled = false

        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = { dismissCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("moreOkButton")
            .performClick()

        assert(dismissCalled)
    }

    @Test
    fun moreDialog_expandableTile_showsUrlWhenClicked() {
        composeTestRule.setContent {
            MoreDialog(
                configRepository = configRepository,
                stateRepository = stateRepository,
                onDismiss = {}
            )
        }

        val aboutUrl = context.getString(R.string.more_about_url)

        composeTestRule.onNodeWithText(aboutUrl)
            .assertDoesNotExist()

        composeTestRule.onNodeWithTag("moreAboutTile")
            .performScrollTo()
            .performClick()

        composeTestRule.onNodeWithText(aboutUrl)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("moreAboutTileOpenButton")
            .performScrollTo()
            .assertIsDisplayed()
    }
}
