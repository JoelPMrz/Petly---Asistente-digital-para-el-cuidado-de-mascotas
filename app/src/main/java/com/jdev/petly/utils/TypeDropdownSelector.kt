package com.jdev.petly.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.jdev.petly.R
import com.jdev.petly.ui.components.BaseOutlinedTextField

@Composable
fun TypeDropdownSelector(
    type: String,
    incompleteType: Boolean,
    onTypeSelected: (String) -> Unit,
) {
    val context = LocalContext.current
    val speciesOptions = context.resources?.getStringArray(R.array.species_options)?.toList() ?: emptyList()

    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown

    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val density = LocalDensity.current

    Box {
        BaseOutlinedTextField(
            value = type,
            placeHolder = stringResource(R.string.dog),
            label = stringResource(R.string.type),
            readOnly = true,
            maxLines = 1,
            isRequired = true,
            isError = incompleteType,
            trailingIcon = icon,
            onClickTrailingIcon = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                }
        ) { }

        Spacer(Modifier.height(10.dp))


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(density) { textFieldSize.width.toDp() })
                .background(MaterialTheme.colorScheme.background)
                .height(160.dp)
        ) {
            speciesOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onTypeSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

