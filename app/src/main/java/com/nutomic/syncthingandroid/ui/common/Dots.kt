package com.nutomic.syncthingandroid.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Dots(
    activeDot: Int,
    dotAmount: Int,
    activeDotColor: Color = MaterialTheme.colorScheme.primary,
    inactiveDotColor: Color = Color.Gray
) {
    for (currentDot in 1..dotAmount) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (currentDot == activeDot) {
                        activeDotColor
                    } else {
                        inactiveDotColor
                    },
                    shape = CircleShape
                )
        )
        if (currentDot != dotAmount) {
            Spacer(modifier = Modifier.size(size = 8.dp))
        }
    }
}
