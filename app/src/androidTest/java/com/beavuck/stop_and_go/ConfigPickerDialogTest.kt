package com.beavuck.stop_and_go

import android.content.Context
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.beavuck.stop_and_go.dialogs.ConfigPickerDialog
import com.beavuck.stop_and_go.repositories.ConfigRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigPickerDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repository: ConfigRepository
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        sharedPreferences =
            context.getSharedPreferences(ConfigRepository.PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
        repository = ConfigRepository(context)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun configPickerDialog_rendersConfigList() {
        repository.bootstrap()

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        val defaultName = context.getString(R.string.config_default_name)
        composeTestRule.onNodeWithTag("configRow_$defaultName").assertIsDisplayed()
        composeTestRule.onNodeWithTag("configRow_HIIT").assertIsDisplayed()
    }

    @Test
    fun configPickerDialog_activeConfigHasRadioSelected() {
        repository.bootstrap()
        repository.createConfig("Other")

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        val defaultName = context.getString(R.string.config_default_name)
        composeTestRule.onNodeWithTag("configRadio_$defaultName").assertIsSelected()
        composeTestRule.onNodeWithTag("configRadio_Other").assertIsNotSelected()
    }

    @Test
    fun configPickerDialog_newButton_opensNameInput() {
        repository.bootstrap()

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("newConfigButton").performClick()
        composeTestRule.onNodeWithTag("nameInputField").assertIsDisplayed()
    }

    @Test
    fun configPickerDialog_renameButton_opensNameInputWithCurrentName() {
        repository.bootstrap()

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("configEdit_Pomodoro").performClick()
        composeTestRule.onNodeWithTag("nameInputField")
            .assertIsDisplayed()
            .assertTextContains("Pomodoro")
    }

    @Test
    fun configPickerDialog_deleteButton_hiddenForActiveConfig() {
        repository.bootstrap()
        repository.createConfig("Other")

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        val defaultName = context.getString(R.string.config_default_name)
        composeTestRule.onNodeWithTag("configDelete_$defaultName").assertDoesNotExist()
        composeTestRule.onNodeWithTag("configDelete_Other").assertIsDisplayed()
    }

    @Test
    fun configPickerDialog_deleteButton_opensConfirmForInactiveConfig() {
        repository.bootstrap()
        repository.createConfig("Temp")

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("configDelete_Temp").performClick()
        composeTestRule.onNodeWithTag("confirmButton").assertIsDisplayed()
    }

    @Test
    fun configPickerDialog_deleteConfirm_removesConfig() {
        repository.bootstrap()
        repository.createConfig("Temp")

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithTag("configDelete_Temp").performClick()
        composeTestRule.onNodeWithTag("confirmButton").performClick()

        composeTestRule.onNodeWithTag("configRow_Temp").assertDoesNotExist()
    }

    @Test
    fun configPickerDialog_tappingRow_activatesAndDismisses() {
        repository.bootstrap()
        repository.createConfig("Other")
        var dismissed = false
        var configChanged = false

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = { configChanged = true },
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag("configRow_Other").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) { dismissed }

        assert(configChanged)
        assert(repository.getActiveConfigName() == "Other")
    }

    @Test
    fun configPickerDialog_dismissButton_callsOnDismiss() {
        repository.bootstrap()
        var dismissed = false

        composeTestRule.setContent {
            ConfigPickerDialog(
                configRepository = repository,
                onConfigChanged = {},
                onDismiss = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithTag("dismissButton").performClick()

        assert(dismissed)
    }
}
