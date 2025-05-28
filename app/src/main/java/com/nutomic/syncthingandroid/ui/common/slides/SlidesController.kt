package com.nutomic.syncthingandroid.ui.common.slides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.Dots

@Composable
fun SlidesController(
    slideState: SlideState, // forcing to hoist the state outside SlidesController
    onBack: () -> Unit,
    onForward: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = slideState.canGoBack && !slideState.backBlocked
            ) {
                Text(stringResource(R.string.back))
            }
            Button(
                onClick = if (slideState.displayFinish) onFinish else onForward,
                enabled = slideState.canGoForward && !slideState.forwardBlocked
            ) {
                Text(stringResource(
                    id = if (slideState.displayFinish) R.string.finish else R.string.cont)
                )
            }
        }
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Dots(activeDot = slideState.currentSlide, dotAmount = slideState.maxSlides)
            }
        }
    }
}
