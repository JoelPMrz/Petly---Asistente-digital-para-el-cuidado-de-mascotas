package com.example.petly.ui.components.pet

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
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petly.R
import com.example.petly.data.models.VeterinaryVisit
import com.example.petly.ui.components.IconCircle
import com.example.petly.ui.components.IconSquare
import com.example.petly.utils.convertWeight
import com.example.petly.utils.formatLocalDateToString
import com.example.petly.utils.formatLocalDateToStringWithDay
import com.example.petly.utils.formatLocalTimeToString
import com.example.petly.utils.truncate
import java.time.LocalDateTime

@Composable
fun VeterinaryVisitsCard(
    veterinaryVisit: VeterinaryVisit?,
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
                icon = Icons.Outlined.MedicalInformation,
                modifier = Modifier.size(30.dp),
                sizeIcon = 20.dp,
                backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondaryContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(R.string.veterinary_visits), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            if(veterinaryVisit != null){
                Column  {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = veterinaryVisit.concept,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    if (!veterinaryVisit.veterinary.isNullOrBlank()) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconCircle(
                                    modifier = Modifier.size(18.dp),
                                    sizeIcon = 18.dp,
                                    icon = ImageVector.vectorResource(id = R.drawable.home_health_24dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = veterinaryVisit.veterinary.toString(),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Justify,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = formatLocalDateToStringWithDay(veterinaryVisit.date),

                            fontWeight = FontWeight.Light,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = formatLocalTimeToString(veterinaryVisit.time),

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