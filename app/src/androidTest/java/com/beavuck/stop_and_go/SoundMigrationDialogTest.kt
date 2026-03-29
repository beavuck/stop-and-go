// (2026-10): Remove this file along with SoundMigrationDialog and AnnouncementsRepository
package com.beavuck.stop_and_go

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beavuck.stop_and_go.dialogs.SoundMigrationDialog
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SoundMigrationDialogTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun dialog_isDisplayed() {
        composeTestRule.setContent {
            SoundMigrationDialog(onDismiss = {})
        }

        composeTestRule.onNodeWithTag("soundMigrationOkButton")
            .assertIsDisplayed()
    }

    @Test
    fun dialog_okButton_dismisses() {
        var dismissCalled = false

        composeTestRule.setContent {
            SoundMigrationDialog(onDismiss = { dismissCalled = true })
        }

        composeTestRule.onNodeWithTag("soundMigrationOkButton")
            .performClick()

        assert(dismissCalled)
    }
}
