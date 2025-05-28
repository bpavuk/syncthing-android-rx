package com.nutomic.syncthingandroid.ui.screens.firstStart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

@Composable
fun NotificationPermissionSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview
@Composable
private fun NotificationPermissionPreview() {
    SyncthingandroidTheme {
        NotificationPermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}
