package com.example.petly.ui.components.pet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petly.data.models.Pet
import com.example.petly.ui.components.IconCircle
import com.example.petly.utils.formatLocalDateToString


@Composable
fun MicrochipCard(
    pet: Pet?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            IconCircle(
                icon = Icons.Outlined.Memory,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Microchip", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = pet?.microchipId.takeUnless { it.isNullOrBlank() } ?: "Sin identificar",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (!pet?.microchipId.isNullOrBlank()) {
                Text(
                    text = pet?.microchipDate?.let { formatLocalDateToString(it) }
                        ?: "Agregar fecha",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}