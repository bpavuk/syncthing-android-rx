package com.nutomic.syncthingandroid.ui.screens.firstStart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

@Composable
fun WelcomeSlide(modifier: Modifier = Modifier, slideState: SlideState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: make it more welcome.
        Text("Welcome!")

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { slideState.forwardBlocked = true }) {
                Text("Block forward")
            }
            Button(onClick = { slideState.forwardBlocked = false }) {
                Text("Unblock forward")
            }
        }
    }
}

@Preview
@Composable
private fun WelcomeSlidePreview() {
    SyncthingandroidTheme {
        WelcomeSlide(
            modifier = Modifier.fillMaxSize(),
            slideState = rememberSlideState(1, 1)
        )
    }
}
