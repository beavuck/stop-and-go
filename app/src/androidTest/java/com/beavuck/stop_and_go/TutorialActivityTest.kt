package com.beavuck.stop_and_go

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.activities.TutorialActivity
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.TutorialRepository
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TutorialActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TutorialActivity>()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var tutorialRepository: TutorialRepository
    private lateinit var configRepository: ConfigRepository

    @Before
    fun setup() {
        tutorialRepository = TutorialRepository(context)
        configRepository = ConfigRepository(context)

        tutorialRepository.resetTutorialCompletionSync()
        configRepository.saveLocale(DEFAULT_LOCALE.code)
    }

    @After
    fun tearDown() {
        tutorialRepository.resetTutorialCompletionSync()
        configRepository.clearLocale()
    }

    @Test
    fun tutorial_startsOnLanguageStep() {
        findSkipButton().assertExists()
        findNextButton().assertExists()
    }

    @Test
    fun skipOnLanguageStep_completesWithDefaultLocale() {
        assertTrue(tutorialRepository.shouldShowTutorial())

        clickSkip()
        waitForCompletion()

        assertFalse(tutorialRepository.shouldShowTutorial())
        assertEquals(DEFAULT_LOCALE.code, configRepository.loadLocale())
    }

    @Test
    fun nextWithoutSelection_usesDefaultLocale() {
        clickNext()

        assertEquals(DEFAULT_LOCALE.code, configRepository.loadLocale())
    }

    @Test
    fun bottomButtons_respectWindowInsets() {
        val navigationBarHeightDp = getSystemNavigationBarHeight()

        val (actualPadding, expectedPadding) = measureBottomPadding(navigationBarHeightDp)

        assertTrue(
            "Bottom buttons padding ($actualPadding dp) should include system insets ($navigationBarHeightDp dp)",
            actualPadding >= expectedPadding - 1f
        )
    }

    private fun findSkipButton() =
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_skip))

    private fun findNextButton() =
        composeTestRule.onNodeWithText(context.getString(R.string.tutorial_next))

    private fun clickSkip() {
        findSkipButton().performClick()
        composeTestRule.waitForIdle()
    }

    private fun clickNext() {
        findNextButton().performClick()
        composeTestRule.waitForIdle()
    }

    private fun selectLocale(locale: SupportedLocale) {
        composeTestRule.onNodeWithTag("locale_${locale.code}").performClick()
        composeTestRule.waitForIdle()
    }

    private fun navigateToGestureStep() {
        clickNext()
        Thread.sleep(300)
    }

    private fun completeAllGestures() {
        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()
        clickNext()

        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        Thread.sleep(50)
        composeTestRule.onRoot().performTouchInput { click() }
        composeTestRule.waitForIdle()
        clickNext()

        composeTestRule.onRoot().performTouchInput { longClick() }
        composeTestRule.waitForIdle()
        clickNext()

        Thread.sleep(300)
    }

    private fun waitForCompletion() {
        Thread.sleep(500)
    }

    private fun getSystemNavigationBarHeight(): Float {
        val decorView = composeTestRule.activity.window.decorView
        val windowInsets = ViewCompat.getRootWindowInsets(decorView)
        val systemBarsInsets = windowInsets?.getInsets(WindowInsetsCompat.Type.systemBars())
        val navigationBarHeight = systemBarsInsets?.bottom ?: 0
        return navigationBarHeight / context.resources.displayMetrics.density
    }

    private fun measureBottomPadding(navigationBarHeightDp: Float): Pair<Float, Float> {
        val skipBounds = findSkipButton().getBoundsInRoot()
        val nextBounds = findNextButton().getBoundsInRoot()
        val rootBounds = composeTestRule.onRoot().getBoundsInRoot()

        val bottomButtonsBottom = maxOf(skipBounds.bottom.value, nextBounds.bottom.value)
        val screenBottom = rootBounds.bottom.value
        val actualPadding = screenBottom - bottomButtonsBottom

        val hardcodedPadding = 24f
        val expectedPadding = hardcodedPadding + navigationBarHeightDp

        return Pair(actualPadding, expectedPadding)
    }
}
