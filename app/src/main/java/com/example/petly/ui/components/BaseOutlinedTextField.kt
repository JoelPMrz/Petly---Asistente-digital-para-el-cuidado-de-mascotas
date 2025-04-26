package com.example.petly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun BaseOutlinedTextField(
    modifier: Modifier = Modifier,
    maxLines : Int?,
    value: String,
    placeHolder: String? = null,
    label: String,
    isRequired: Boolean = false,
    readOnly : Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon : ImageVector? = null,
    singleLine: Boolean? = false,
    keyboardOptions : KeyboardOptions? = null,
    onClickTrailingIcon: (() -> Unit)? = null,
    onUserChange: (String) -> Unit
) {
    if (singleLine != null && maxLines != null) {
        Box(modifier = modifier) {
            OutlinedTextField(
                value = value,
                onValueChange = { onUserChange(it) },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    if (!placeHolder.isNullOrEmpty()) {
                        Text(text = placeHolder, fontWeight = FontWeight.ExtraLight)
                    }
                },
                label = {
                    Text(
                        label,
                        fontWeight = FontWeight.Medium,
                        fontStyle = FontStyle.Italic
                    )
                },
                leadingIcon = leadingIcon?.let {
                    {
                        Icon(
                            imageVector = it,
                            contentDescription = it.name,
                        )
                    }
                },
                trailingIcon = trailingIcon?.let {
                    {
                        Icon(
                            modifier = Modifier.clickable {
                                onClickTrailingIcon?.invoke()
                            },
                            imageVector = it,
                            contentDescription = it.name,
                        )
                    }
                },
                maxLines = maxLines,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions ?: KeyboardOptions.Default,
                readOnly = readOnly
            )

            if (isRequired) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding( top = 8.dp)
                        .size(18.dp)
                        .background(
                            color = if(value.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            shape = RoundedCornerShape(0,14,0,100)
                        ),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Text(
                        text = "*",
                        modifier = Modifier.padding(end = 3.dp),
                        color = if(value.isNotEmpty())MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
