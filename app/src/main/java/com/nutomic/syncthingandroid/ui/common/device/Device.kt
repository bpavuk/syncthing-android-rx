package com.nutomic.syncthingandroid.ui.common.device

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import syncthingrest.model.device.DeviceID

data class DeviceCardState(
    val state: DeviceCardSyncState,
    val view: DeviceCardDataView
)

sealed interface DeviceCardSyncState {
    data object UpToDate : DeviceCardSyncState
    data object Disconnected : DeviceCardSyncState
    data class InProgress(val progress: Float) : DeviceCardSyncState
    data object Error : DeviceCardSyncState
    data object Paused : DeviceCardSyncState
}

data class DeviceCardDataView(
    val label: String,
    val deviceID: DeviceID,
)

@Composable
fun DeviceCard(
    device: DeviceCardState,
    modifier: Modifier = Modifier,
    onDeviceCardClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (device.state is DeviceCardSyncState.Error) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }
    )

    Column(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(shape = MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onDeviceCardClick)
            .padding(16.dp) then modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.view.label,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = device.view.deviceID.value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = null
            )
        }

        val stateLabel = when (device.state) {
            DeviceCardSyncState.Error -> R.string.state_error
            DeviceCardSyncState.Disconnected -> R.string.device_disconnected
            is DeviceCardSyncState.InProgress -> R.string.state_syncing_general
            DeviceCardSyncState.UpToDate -> R.string.device_up_to_date
            DeviceCardSyncState.Paused -> R.string.device_paused
        }
        Text(text = stringResource(id = stateLabel))

        if (device.state is DeviceCardSyncState.InProgress) {
            Box(
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                LinearProgressIndicator(
                    progress = { device.state.progress },
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun DeviceCardUpToDatePreview() {
    SyncthingandroidTheme {
        DeviceCard(
            device = DeviceCardState(
                state = DeviceCardSyncState.UpToDate,
                view = DeviceCardDataView(
                    label = "My Android Device",
                    deviceID = DeviceID("ABCD123-EFGH456-IJKL789-MNOP012-QRST345-UVWX678-YZAB901"),
                )
            )
        )
    }
}

@Preview
@Composable
private fun DeviceCardErrorPreview() {
    SyncthingandroidTheme {
        DeviceCard(
            device = DeviceCardState(
                state = DeviceCardSyncState.Error,
                view = DeviceCardDataView(
                    label = "My Linux Server",
                    deviceID = DeviceID("ABCD123-EFGH456-IJKL789-MNOP012-QRST345-UVWX678-YZAB901"),
                )
            )
        )
    }
}

@Preview
@Composable
private fun DeviceCardInProgressPreview() {
    SyncthingandroidTheme {
        DeviceCard(
            device = DeviceCardState(
                state = DeviceCardSyncState.InProgress(progress = 0.42f),
                view = DeviceCardDataView(
                    label = "My Windows PC",
                    deviceID = DeviceID("ABCD123-EFGH456-IJKL789-MNOP012-QRST345-UVWX678-YZAB901"),
                )
            )
        )
    }
}

@Preview
@Composable
private fun DeviceCardDisconnectedPreview() {
    SyncthingandroidTheme {
        DeviceCard(
            device = DeviceCardState(
                state = DeviceCardSyncState.Disconnected,
                view = DeviceCardDataView(
                    label = "My macOS Laptop",
                    deviceID = DeviceID("ABCD123-EFGH456-IJKL789-MNOP012-QRST345-UVWX678-YZAB901"),
                )
            )
        )
    }
}

@Preview
@Composable
private fun DeviceCardPausedPreview() {
    SyncthingandroidTheme {
        DeviceCard(
            device = DeviceCardState(
                state = DeviceCardSyncState.Paused,
                view = DeviceCardDataView(
                    label = "My Android Device (Paused)",
                    deviceID = DeviceID("ABCD123-EFGH456-IJKL789-MNOP012-QRST345-UVWX678-YZAB901"),
                )
            )
        )
    }
}
