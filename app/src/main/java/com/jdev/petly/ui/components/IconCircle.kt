package com.jdev.petly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconCircle(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    sizeIcon : Dp = 24.dp,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(30.dp)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
            Icon(
                modifier = Modifier.size(
                    sizeIcon
                ).padding(2.dp),
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
            )
    }
}

@Composable
fun IconSquare(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
    sizeIcon : Dp = 24.dp,
    backgroundColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .size(30.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.size(
                sizeIcon
            ),
            contentDescription = null,
            tint = contentColor
        )
    }
}