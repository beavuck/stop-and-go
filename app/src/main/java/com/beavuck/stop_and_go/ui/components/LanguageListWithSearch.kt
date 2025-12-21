package com.beavuck.stop_and_go.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.config.SupportedLocale

@Composable
fun LanguageListWithSearch(
    selectedLocale: SupportedLocale?,
    onLocaleSelected: (SupportedLocale) -> Unit,
    modifier: Modifier = Modifier,
    showSearch: Boolean = true,
    itemFontSize: androidx.compose.ui.unit.TextUnit = 16.sp
) {
    var searchQuery by remember { mutableStateOf("") }
    val locales = SupportedLocale.entries
    val context = LocalContext.current

    val filteredLocales = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            locales
        } else {
            locales.filter { locale ->
                val displayName = locale.getDisplayName(context)
                val query = searchQuery.trim()
                displayName.contains(query, ignoreCase = true) ||
                        locale.code.contains(query, ignoreCase = true)
            }
        }
    }

    Column(modifier = modifier) {
        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("languageSearchField"),
                placeholder = { Text(stringResource(R.string.search_language)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        TextButton(
                            onClick = { searchQuery = "" },
                            modifier = Modifier.testTag("clearSearchButton")
                        ) {
                            Text(stringResource(R.string.clear_search))
                        }
                    }
                },
                singleLine = true
            )
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            if (filteredLocales.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_languages_found),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                filteredLocales.forEach { locale ->
                    LocaleRadioItem(
                        locale = locale,
                        isSelected = locale == selectedLocale,
                        onSelect = { onLocaleSelected(locale) },
                        fontSize = itemFontSize
                    )
                }
            }
        }
    }
}

@Composable
private fun LocaleRadioItem(
    locale: SupportedLocale,
    isSelected: Boolean,
    onSelect: () -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit = 16.sp
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
            fontSize = fontSize
        )
    }
}
