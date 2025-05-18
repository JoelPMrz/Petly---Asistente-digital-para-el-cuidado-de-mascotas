package com.example.petly.ui.components.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.petly.R

@Composable
fun PetNotExistsDialog(
    navigateTo: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            navigateTo()
        },
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.pet_not_exists_dialog_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pet_not_exists),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pet_not_exists_dialog_description),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(onClick = { navigateTo() }) {
                Text(stringResource(R.string.understood))
            }
        }
    )
}
