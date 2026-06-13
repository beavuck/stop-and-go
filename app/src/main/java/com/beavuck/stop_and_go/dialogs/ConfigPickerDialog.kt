package com.beavuck.stop_and_go.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import com.beavuck.stop_and_go.model.timer.TimerConstants.DEBOUNCE_DELAY
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.timer.NamedConfig
import com.beavuck.stop_and_go.repositories.ConfigRepository
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ConfigPickerDialog(
    configRepository: ConfigRepository,
    onConfigChanged: () -> Unit,
    onDismiss: () -> Unit,
) {
    val configs = remember { mutableStateOf(configRepository.listConfigs()) }
    val activeConfigName = remember { mutableStateOf(configRepository.getActiveConfigName()) }
    val dialogState = remember { mutableStateOf<DialogState>(DialogState.None) }
    val scope = rememberCoroutineScope()

    fun selectConfig(config: NamedConfig) {
        configRepository.setActiveConfig(config.name)
        activeConfigName.value = config.name
        scope.launch {
            delay(DEBOUNCE_DELAY.milliseconds)
            onConfigChanged()
            onDismiss()
        }
    }

    when (val state = dialogState.value) {
        is DialogState.Create -> NameInputDialog(
            title = stringResource(R.string.config_new),
            initialValue = "",
            onConfirm = { name ->
                configRepository.createConfig(name)
                configs.value = configRepository.listConfigs()
                dialogState.value = DialogState.None
                selectConfig(configRepository.listConfigs().first { it.name == name })
            },
            onDismiss = { dialogState.value = DialogState.None }
        )

        is DialogState.Rename -> NameInputDialog(
            title = stringResource(R.string.config_rename),
            initialValue = state.currentName,
            onConfirm = { name ->
                configRepository.renameConfig(state.currentName, name)
                configs.value = configRepository.listConfigs()
                activeConfigName.value = configRepository.getActiveConfigName()
                dialogState.value = DialogState.None
            },
            onDismiss = { dialogState.value = DialogState.None }
        )

        is DialogState.Delete -> ConfirmDialog(
            title = stringResource(R.string.config_delete_confirm_title),
            message = stringResource(R.string.config_delete_confirm_message, state.targetName),
            onConfirm = {
                configRepository.deleteConfig(state.targetName)
                configs.value = configRepository.listConfigs()
                dialogState.value = DialogState.None
            },
            onDismiss = { dialogState.value = DialogState.None }
        )

        DialogState.None -> Unit
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.config_picker_title))
                IconButton(
                    onClick = { dialogState.value = DialogState.Create },
                    modifier = Modifier.testTag("newConfigButton")
                ) {
                    Icon(
                        painter = painterResource(R.drawable.plus_circle),
                        contentDescription = stringResource(R.string.config_new)
                    )
                }
            }
        },
        text = {
            LazyColumn(modifier = Modifier.testTag("configList")) {
                items(configs.value, key = { it.name }) { config ->
                    ConfigRow(
                        config = config,
                        isActive = config.name == activeConfigName.value,
                        onSelect = { selectConfig(config) },
                        onRename = { dialogState.value = DialogState.Rename(config.name) },
                        onDelete = { dialogState.value = DialogState.Delete(config.name) }
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismissButton")
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun ConfigRow(
    config: NamedConfig,
    isActive: Boolean,
    onSelect: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("configRow_${config.name}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isActive,
            onClick = onSelect,
            modifier = Modifier.testTag("configRadio_${config.name}")
        )
        Text(
            text = config.name,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = onRename,
            modifier = Modifier.testTag("configEdit_${config.name}")
        ) {
            Icon(
                painter = painterResource(R.drawable.pencil),
                contentDescription = stringResource(R.string.config_rename)
            )
        }
        if (!isActive) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("configDelete_${config.name}")
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = stringResource(R.string.config_delete)
                )
            }
        }
    }
}

private sealed interface DialogState {
    data object None : DialogState
    data object Create : DialogState
    data class Rename(val currentName: String) : DialogState
    data class Delete(val targetName: String) : DialogState
}
