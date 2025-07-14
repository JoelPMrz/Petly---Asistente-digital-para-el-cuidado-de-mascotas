package com.jdev.petly.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jdev.petly.R

@Composable
fun UpdateAppDialog(
    isMandatory: Boolean,
    latestVersion: String,
    minimumVersion : String,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isMandatory) onDismiss() },
        title = {
            Text(
                if (isMandatory) {
                    stringResource(R.string.incompatible_version)
                } else {
                    stringResource(R.string.update_available)
                }
            )
        },
        text = {
            Text(
                if (isMandatory) {
                    stringResource(R.string.incompatible_version_description, minimumVersion)
                } else {
                    stringResource(R.string.update_available_description, latestVersion)
                }
            )
        },
        confirmButton = {
            Button(onClick = onUpdate) {
                Text(stringResource(R.string.update))
            }
        },
        dismissButton = if (isMandatory) null else {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.form_cancel_btn))
                }
            }
        }
    )
}