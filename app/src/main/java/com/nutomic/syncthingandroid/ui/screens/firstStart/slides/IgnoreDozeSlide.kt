package com.nutomic.syncthingandroid.ui.screens.firstStart

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
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import com.nutomic.syncthingandroid.util.compose.doze.rememberDozePermissionState

@Composable
fun IgnoreDozePermissionSlide(modifier: Modifier = Modifier, slideState: SlideState) {
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
            // Battery management may not like Syncthing
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                slideState.forwardBlocked = false
                // All set!
            } else {
                val dozePermissionState = rememberDozePermissionState()

                if (dozePermissionState.granted) {
                    // All set!
                    Text("Battery permissions are set")
                    slideState.forwardBlocked = false
                } else {
                    Text("Please grant battery permissions")
                    Button(onClick = { dozePermissionState.requestPermission() }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun IgnoreDozePermissionPreview() {
    SyncthingandroidTheme {
        IgnoreDozePermissionSlide(
            modifier = Modifier.fillMaxSize(),
            slideState = rememberSlideState(1, 1)
        )
    }
}
