package com.jdev.petly.ui.components.pet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jdev.petly.R
import com.jdev.petly.data.models.Event
import com.jdev.petly.ui.components.IconCircle
import com.jdev.petly.utils.formatLocalDateToStringWithDay
import com.jdev.petly.utils.formatLocalTimeToString

@Composable
fun EventsCard(
    event: Event?,
    modifier: Modifier,
    onClick: () -> Unit,
) {
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
                icon = Icons.Outlined.Event,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.upcoming_event), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            if(event != null){
                Column  {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = event.concept,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = formatLocalDateToStringWithDay(event.date),

                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = formatLocalTimeToString(event.time),

                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                    }
                }
            }else{
                Text(
                    text = stringResource(R.string.register_new_veterinay_visists),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        }
    }
}