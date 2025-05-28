package com.nutomic.syncthingandroid.ui.screens.firstStart

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.common.slides.SlideState
import com.nutomic.syncthingandroid.ui.common.slides.rememberSlideState
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

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
                val packageName = LocalContext.current.packageName
                val powerManager = LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager
                var dozePermissionGranted by remember { mutableStateOf(powerManager.isIgnoringBatteryOptimizations(packageName)) }

                val requestDozePermission = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    dozePermissionGranted = powerManager.isIgnoringBatteryOptimizations(packageName)
                    Log.d("IgnoreDozeSlide", result.data.toString())
                }

                if (dozePermissionGranted) {
                    // All set!
                    Text("Battery permissions are set")
                    slideState.forwardBlocked = false
                } else {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                    intent.setData("package:$packageName".toUri())
                    Text("Please grant battery permissions")
                    Button(onClick = { requestDozePermission.launch(intent) }) {
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
