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
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionSlide(modifier: Modifier = Modifier) {
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
            // Wi-Fi and location permissions
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    val fineLocationPermission = rememberPermissionState(
                        permission = Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val backgroundLocationPermission = rememberPermissionState(
                        permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    if (fineLocationPermission.status.isGranted && backgroundLocationPermission.status.isGranted) {
                        Text("All set! Location access enabled.")
                    } else {
                        Text(stringResource(R.string.location_permission_desc))
                        Text(stringResource(R.string.location_permission_desc_api_29))
                        Button(onClick = {
                            fineLocationPermission.launchPermissionRequest()
                            backgroundLocationPermission.launchPermissionRequest()
                        }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    }

                }
                else -> {
                    val locationPermission = rememberPermissionState(
                        permission = Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if (locationPermission.status.isGranted) {
                        Text("All set!")
                    } else {
                        Text(stringResource(R.string.location_permission_desc))
                        Button(onClick = {
                            locationPermission.launchPermissionRequest()
                        }) {
                            Text(stringResource(R.string.grant_permission))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LocationPermissionPreview() {
    SyncthingandroidTheme {
        LocationPermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}
