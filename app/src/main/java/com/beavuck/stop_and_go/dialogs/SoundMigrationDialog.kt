// (2026-10): Remove this file, AnnouncementsRepository, SoundMigrationDialogTest,
//  and the announcement block in MainActivity.onCreate — one-time sound migration announcement.
package com.beavuck.stop_and_go.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.beavuck.stop_and_go.R

@Composable
fun SoundMigrationDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(stringResource(R.string.sound_migration_announcement)) },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("soundMigrationOkButton")
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
