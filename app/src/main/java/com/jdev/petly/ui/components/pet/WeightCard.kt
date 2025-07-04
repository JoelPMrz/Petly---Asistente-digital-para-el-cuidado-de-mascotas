package com.jdev.petly.ui.components.pet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jdev.petly.R
import com.jdev.petly.data.models.Weight
import com.jdev.petly.ui.components.IconCircle
import com.jdev.petly.utils.convertWeight
import com.jdev.petly.utils.formatLocalDateToString
import com.jdev.petly.utils.truncate
import com.jdev.petly.viewmodel.PreferencesViewModel

@Composable
fun WeightCard(
    weight: Weight?,
    modifier: Modifier,
    onClick: () -> Unit,
    preferencesViewModel: PreferencesViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        preferencesViewModel.reloadUnitPreference()
    }
    val context = LocalContext.current
    val selectedUnit by preferencesViewModel.selectedUnit.collectAsState()
    var convertedWeight by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(weight, selectedUnit) {
        if (weight != null) {
            convertedWeight =
                convertWeight(weight.value, weight.unit, selectedUnit).truncate(2).toString()
            dateString = formatLocalDateToString(weight.date)
        } else {
            convertedWeight = context.getString(R.string.add_weight)
            dateString = ""
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 120.dp)
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
                icon = Icons.Outlined.MonitorWeight,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.current_weight), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text(
                text = "$convertedWeight $selectedUnit",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = dateString,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}