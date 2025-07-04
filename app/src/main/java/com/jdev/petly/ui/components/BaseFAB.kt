package com.jdev.petly.ui.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun BaseFAB(onClick: () -> Unit, imageVector: ImageVector) {
    FloatingActionButton(
        onClick = {
            onClick()
        }
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
    }
}