package com.nutomic.syncthingandroid.ui.screens.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.ui.common.device.DeviceCard
import com.nutomic.syncthingandroid.ui.common.device.DeviceCardViewModelImpl
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(viewModel) {
        viewModel.updateDevices()
    }
    val uiState = viewModel.uiState.collectAsState().value

    Column(Modifier.padding(16.dp) then modifier) {
        when (uiState) {
            DeviceListUIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            DeviceListUIState.FailedToLoad -> {
                Text("Failed to load devices. You may need support at this point...")
            }
            is DeviceListUIState.Success -> {
                if (uiState.deviceIDs.isEmpty()) {
                    Text("No devices found.")
                } else {
                    for (deviceID in uiState.deviceIDs) {
                        DeviceCard(
                            deviceID = deviceID,
                            viewModel = koinViewModel<DeviceCardViewModelImpl>(key = deviceID.value)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}
