package com.beavuck.stop_and_go.dialogs

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.beavuck.stop_and_go.R
import com.beavuck.stop_and_go.model.timer.TimerConfig
import com.beavuck.stop_and_go.repositories.ConfigRepository
import com.beavuck.stop_and_go.repositories.StateRepository
import com.google.android.play.core.review.ReviewManagerFactory

private const val TAG = "MoreDialog"

@Composable
fun MoreDialog(
    configRepository: ConfigRepository,
    stateRepository: StateRepository,
    onDismiss: () -> Unit,
    onReset: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShareButton()

                RateButton()

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                ExpandableLinkTile(
                    title = stringResource(R.string.more_about),
                    url = stringResource(R.string.more_about_url),
                    iconRes = R.drawable.question_mark,
                    testTag = "moreAboutTile"
                )

                ExpandableLinkTile(
                    title = stringResource(R.string.more_privacy),
                    url = stringResource(R.string.more_privacy_url),
                    iconRes = R.drawable.shield_user,
                    testTag = "morePrivacyTile"
                )

                ExpandableLinkTile(
                    title = stringResource(R.string.more_license),
                    url = stringResource(R.string.more_license_url),
                    iconRes = R.drawable.parchment,
                    testTag = "moreLicenseTile"
                )

                ExpandableLinkTile(
                    title = stringResource(R.string.more_tip),
                    url = stringResource(R.string.more_tip_url),
                    iconRes = R.drawable.coins,
                    testTag = "moreTipTile"
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                ResetButton(
                    configRepository = configRepository,
                    stateRepository = stateRepository,
                    onReset = onReset,
                )

                AppVersion()
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("moreOkButton")
            ) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
private fun ShareButton() {
    val context = LocalContext.current
    val shareText = stringResource(R.string.more_share_text)

    Button(
        onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(shareIntent, null))
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("moreShareButton")
    ) {
        Text(stringResource(R.string.more_share))
    }
}

@Composable
private fun RateButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            val activity = context.findActivity() ?: return@Button
            val manager = ReviewManagerFactory.create(context)
            manager.requestReviewFlow().addOnSuccessListener { reviewInfo ->
                manager.launchReviewFlow(activity, reviewInfo)
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to launch review flow", e)
                Toast.makeText(context, R.string.more_rate_failed, Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("moreRateButton")
    ) {
        Text(stringResource(R.string.more_rate))
    }
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
private fun ResetButton(
    configRepository: ConfigRepository,
    stateRepository: StateRepository,
    onReset: (() -> Unit)?,
) {
    val context = LocalContext.current
    val resetSuccessMessage = stringResource(R.string.more_reset_success)
    Button(
        onClick = {
            configRepository.initPresets()
            stateRepository.clearState()
            onReset?.invoke()
            Toast.makeText(context, resetSuccessMessage, Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("moreResetButton"),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(stringResource(R.string.more_reset_settings))
    }
}

@Composable
private fun AppVersion() {
    val context = LocalContext.current
    val versionName = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
                    .versionName
            } else {
                context.packageManager
                    .getPackageInfo(context.packageName, 0)
                    .versionName
            }
        } catch (_: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }

    Text(
        text = "v$versionName",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}

@Composable
private fun ExpandableLinkTile(
    title: String,
    url: String,
    iconRes: Int? = null,
    testTag: String? = null
) {
    val context = LocalContext.current
    val expanded = remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded.value = !expanded.value }
            .then(testTag?.let { Modifier.testTag(it) } ?: Modifier),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            AnimatedVisibility(
                visible = expanded.value,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = url,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(testTag?.let { Modifier.testTag("${it}OpenButton") } ?: Modifier)
                    ) {
                        Text(stringResource(R.string.more_open))
                    }
                }
            }
        }
    }
}
