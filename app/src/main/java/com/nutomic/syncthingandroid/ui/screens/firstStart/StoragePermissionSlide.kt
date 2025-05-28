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
import com.google.accompanist.permissions.shouldShowRationale
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import com.nutomic.syncthingandroid.util.compose.rememberScopedStoragePermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermissionSlide(modifier: Modifier = Modifier, slideState: SlideState) {
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
            // Let's set up your storage
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // handle pre-Scoped Storage permissions
                val storagePermissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionState.status.isGranted) {
                    // All set!
                    Text("All set!")
                } else {
                    // TODO: use string res
                    val text = if (storagePermissionState.status.shouldShowRationale) {
                        "Storage permission is required for Syncthing to function. Otherwise, we " +
                                "can't even sync a thing!"
                    } else {
                        "Please, grant the permission for storage access. With that permission, we " +
                                "will be able to sync your things."
                    }
                    Text(text)
                    Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            } else {
                // handle Scoped Storage
                val storagePermissionState = rememberScopedStoragePermissionState()

                Text("Let's set up Scoped Storage permissions!")
                Text(storagePermissionState.granted.toString())
                if (!storagePermissionState.granted) {
                    Button(onClick = { storagePermissionState.requestPermission() }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StoragePermissionPreview() {
    SyncthingandroidTheme {
        StoragePermissionSlide(
            modifier = Modifier.fillMaxSize(),
            slideState = rememberSlideState(1, 1)
        )
    }
}
