package com.nutomic.syncthingandroid.ui.screens.firstStart

import android.Manifest
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionSlide(modifier: Modifier = Modifier, slideState: SlideState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        slideState.forwardBlocked = true

        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            // Notification permissions
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    val notificationPermission = rememberPermissionState(
                        permission = Manifest.permission.POST_NOTIFICATIONS
                    )
                    if (notificationPermission.status.isGranted) {
                        Text("Enabled!")
                    } else {
                        Text(stringResource(R.string.notification_permission_desc))
                        Button(onClick = {
                            notificationPermission.launchPermissionRequest()
                        }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    }
                }
                else -> {
                    Text("No notification permission required on this device.")
                }
            }
        }
    }
}

@Preview
@Composable
private fun NotificationPermissionPreview() {
    SyncthingandroidTheme {
        NotificationPermissionSlide(
            modifier = Modifier.fillMaxSize(),
            slideState = rememberSlideState(1, 1)
        )
    }
}
