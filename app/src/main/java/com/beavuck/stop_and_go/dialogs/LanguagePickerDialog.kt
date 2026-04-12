package com.beavuck.stop_and_go.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale
import com.beavuck.stop_and_go.ui.components.LanguageListWithSearch

@Composable
fun LanguagePickerDialog(
    currentLocale: SupportedLocale = DEFAULT_LOCALE,
    onLocaleSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val selectedLocale = remember { mutableStateOf(currentLocale) }

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            LanguageListWithSearch(
                selectedLocale = selectedLocale.value,
                onLocaleSelected = { locale ->
                    selectedLocale.value = locale
                    onLocaleSelected(locale.code)
                }
            )
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancelButton")
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}
