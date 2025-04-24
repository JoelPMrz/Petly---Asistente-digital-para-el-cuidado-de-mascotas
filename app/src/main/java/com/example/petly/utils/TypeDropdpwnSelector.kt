package com.example.petly.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.petly.ui.components.BaseOutlinedTextField

@Composable
fun TypeDropdownSelector(
    type: String,
    onTypeSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown

    Box {
        BaseOutlinedTextField(
            value = type,
            placeHolder = "Perro",
            label = "Especie",
            readOnly = true,
            maxLines = 1,
            trailingIcon = icon,
            onClickTrailingIcon = { expanded = !expanded }
        ) { }

        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(200.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
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
}



val speciesOptions = listOf(
    "Araña",
    "Burro",
    "Caballo",
    "Cabra",
    "Camaleón",
    "Cangrejo",
    "Cerdo",
    "Chinchilla",
    "Cobaya",
    "Conejo",
    "Erizo",
    "Escorpión",
    "Ferret",
    "Gallina",
    "Gato",
    "Gecko",
    "Hámster",
    "Hurón",
    "Iguana",
    "Lagarto",
    "Oveja",
    "Paloma",
    "Pato",
    "Pájaro",
    "Pez",
    "Perro",
    "Rata",
    "Ratón",
    "Serpiente",
    "Tortuga"
)
