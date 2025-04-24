package com.example.petly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.petly.R


@Composable
fun BaseOutlinedTextField(
    modifier: Modifier = Modifier,
    maxLines : Int?,
    value: String,
    placeHolder: String? = null,
    label: String,
    readOnly : Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon : ImageVector? = null,
    singleLine: Boolean? = false,
    keyboardOptions : KeyboardOptions? = null,
    onClickTrailingIcon: (() -> Unit)? = null,
    onUserChange: (String) -> Unit
) {
    if (singleLine != null) {
        if (maxLines != null) {
            OutlinedTextField(
                value = value,
                onValueChange = { onUserChange(it) },
                modifier = modifier.fillMaxWidth(),
                placeholder = {
                    if (!placeHolder.isNullOrEmpty()) {
                        Text(
                            text = placeHolder,
                        )
                    }
                },
                label = {
                    Text(
                        label,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic
                    )
                },
                leadingIcon = if (leadingIcon != null) {
                    {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = leadingIcon.name,
                        )
                    }
                } else null,
                trailingIcon = if (trailingIcon != null) {
                    {
                        Icon(
                            modifier = Modifier.clickable{
                                if (onClickTrailingIcon != null) {
                                    onClickTrailingIcon()
                                }
                            },
                            imageVector = trailingIcon,
                            contentDescription = trailingIcon.name,
                        )
                    }
                } else null,
                maxLines = maxLines,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
                readOnly = readOnly
            )
        }
    }
}
