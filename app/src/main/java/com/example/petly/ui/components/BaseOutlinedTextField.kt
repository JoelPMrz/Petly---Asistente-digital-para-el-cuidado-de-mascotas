package com.example.petly.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.petly.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseOutlinedTextField(
    value: String,
    placeHolder: String? = null,
    label: String,
    leadingIcon: ImageVector,
    onUserChange: (String) -> Unit
) {
   OutlinedTextField(
        value = value,
        onValueChange = { onUserChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            if (!placeHolder.isNullOrEmpty()) {
                Text(
                    text = placeHolder,
                    //color = colorResource(id = R.color.blue80)
                )
            }
        },
        label = {
            Text(
                label,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                //color = colorResource(id = R.color.blue100)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIcon.name,
                //tint = colorResource(id = R.color.blue100)
            )
        },
       /*
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = colorResource(id = R.color.blue50),  // Cambia el color de fondo
            focusedBorderColor = colorResource(id = R.color.blue100), // Cambia el color del borde cuando está enfocado
            unfocusedBorderColor = colorResource(id = R.color.blue50), // Cambia el color del borde cuando no está enfocado
            focusedTextColor = colorResource(id = R.color.blue100), // Cambia el color del texto cuando está enfocado
            unfocusedTextColor = colorResource(id = R.color.blue100),
            //cursorColor = colorResource(id = R.color.blue100)// Cambia el color del borde cuando no está enfocado
        )

        */
    )
}
