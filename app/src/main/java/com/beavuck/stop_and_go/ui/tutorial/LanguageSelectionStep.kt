package com.beavuck.stop_and_go.ui.tutorial

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.config.DEFAULT_LOCALE
import com.beavuck.stop_and_go.config.SupportedLocale

@Composable
fun LanguageSelectionStep(
    onLanguageSelected: (String) -> Unit,
    onSkip: () -> Unit
) {
    var selectedLocale by rememberSaveable { mutableStateOf<SupportedLocale?>(null) }
    val locales = SupportedLocale.entries

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 32.dp, end = 32.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.tutorial_language_title),
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.tutorial_language_description),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            locales.forEach { locale ->
                LocaleRadioItem(
                    locale = locale,
                    isSelected = locale == selectedLocale,
                    onSelect = {
                        selectedLocale = locale
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.tutorial_skip))
            }

            Button(
                onClick = {
                    val locale = selectedLocale ?: DEFAULT_LOCALE
                    onLanguageSelected(locale.code)
                }
            ) {
                Text(stringResource(R.string.tutorial_next))
            }
        }
    }
}

@Composable
private fun LocaleRadioItem(
    locale: SupportedLocale,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .testTag("locale_${locale.code}")
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        Text(
            text = locale.getDisplayName(context),
            modifier = Modifier.padding(start = 16.dp),
            fontSize = 18.sp
        )
    }
}
