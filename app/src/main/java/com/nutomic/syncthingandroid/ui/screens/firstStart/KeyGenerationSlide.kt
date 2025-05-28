package com.nutomic.syncthingandroid.ui.screens.firstStart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.screens.firstStart.keygen.KeyGenerationState
import com.nutomic.syncthingandroid.ui.screens.firstStart.keygen.KeyGenerationViewModel
import com.nutomic.syncthingandroid.ui.screens.firstStart.keygen.PreviewKeyGenerationViewModel
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

@Composable
fun KeyGenerationSlide(
    slideState: SlideState,
    modifier: Modifier = Modifier,
    viewModel: KeyGenerationViewModel,
) {
    val state = viewModel.state
    slideState.forwardBlocked = true
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            // Key generation
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = when (state) {
                KeyGenerationState.ExecutableNotFound -> R.string.executable_not_found
                KeyGenerationState.Failed -> R.string.config_create_failed
                KeyGenerationState.InProgress -> R.string.web_gui_creating_key
                KeyGenerationState.Success -> R.string.key_generation_success
            }
            if (state == KeyGenerationState.Success) slideState.forwardBlocked = false

            Text(stringResource(textToShow))
        }
        LaunchedEffect(Unit) {
            viewModel.launchKeyGeneration()
        }
    }
}

@Preview
@Composable
private fun KeyGenerationSlidePreview() {
    SyncthingandroidTheme {
        KeyGenerationSlide(
            slideState = rememberSlideState(1, 1),
            modifier = Modifier.fillMaxSize(),
            viewModel = PreviewKeyGenerationViewModel()
        )
    }
}
