package com.nutomic.syncthingandroid.ui.common.progressBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val normalizedProgress = when {
        progress > 1f -> 1f
        progress < 0f -> 0f
        else -> progress
    }

    Row(
        modifier = Modifier
            .height(16.dp)
            .clip(MaterialTheme.shapes.medium) then modifier
    ) {
        Box(
            modifier = Modifier
                .background(color = Color.Green)
                .weight(normalizedProgress)
        )
        Box(
            modifier = Modifier
                .background(color = Color.Gray)
                .weight(1f - normalizedProgress)
        )
    }
}