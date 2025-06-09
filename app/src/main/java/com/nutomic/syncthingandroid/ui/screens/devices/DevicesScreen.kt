package com.nutomic.syncthingandroid.ui.screens.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.ui.common.device.DeviceCard
import kotlinx.coroutines.delay

@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel,
    modifier: Modifier = Modifier
) {
    val devices = viewModel.devices

    // TODO: once [SyncthingService] is rewritten with Coroutines and Flow, replace with
    //  flows-as-states
    LaunchedEffect(null) {
        while (true) {
            viewModel.updateDevices()
            delay(2000)
        }
    }

    Column(Modifier.padding(16.dp) then modifier) {
        if (devices.isEmpty()) {
            Text("No devices found.")
        } else {
            for (device in devices) {
                DeviceCard(
                    device = device
                )
            }
        }
    }
}
