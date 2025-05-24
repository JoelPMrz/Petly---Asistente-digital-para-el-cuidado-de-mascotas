package com.example.petly.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.petly.R


@Composable
fun PasswordOutlinedTextField(
    value: String,
    label : String? = null,
    leadingIcon : ImageVector? = null,
    onUserChange: (String) -> Unit
) {
    var passwordHidden: Boolean by remember { mutableStateOf(true) }
    OutlinedTextField(
        value = value,
        onValueChange = { onUserChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label ?: stringResource(R.string.password), fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            Icon(
                imageVector = if (passwordHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = "Lock",
                modifier = Modifier.clickable {
                    passwordHidden = !passwordHidden
                }
            )
        },
        leadingIcon = {
            Icon(
                imageVector =  leadingIcon ?: Icons.Default.Lock,
                contentDescription = "Lock icon"
            )
        }
    )
}