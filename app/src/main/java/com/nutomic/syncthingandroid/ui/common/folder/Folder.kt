package com.nutomic.syncthingandroid.ui.common.folder

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
import androidx.compose.material.icons.filled.Folder
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
import com.nutomic.syncthingandroid.ui.theme.yellowPrimaryContainer
import syncthingrest.model.folder.FolderID

data class FolderCardState(
    val syncState: FolderCardSyncState,
    val id: FolderID,
    val label: String,
    val path: String
)

sealed interface FolderCardSyncState {
    data object UpToDate : FolderCardSyncState
    data class InProgress(val progress: Float) : FolderCardSyncState
    data object Scanning : FolderCardSyncState
    data object Error : FolderCardSyncState
    data object Paused : FolderCardSyncState
}

@Composable
fun FolderCard(
    folder: FolderCardState,
    modifier: Modifier = Modifier,
    onFolderCardClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (folder.syncState) {
            is FolderCardSyncState.Error -> MaterialTheme.colorScheme.errorContainer
            is FolderCardSyncState.Paused -> yellowPrimaryContainer()
            else -> MaterialTheme.colorScheme.primaryContainer
        }
    )

    Column(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.extraLarge
            )
            .clip(shape = MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onFolderCardClick)
            .padding(16.dp) then modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.label,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = folder.path,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis
                )
            }
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null
            )
        }

        val stateLabel = when (folder.syncState) {
            FolderCardSyncState.Error -> R.string.state_error
            is FolderCardSyncState.InProgress -> R.string.state_syncing_general
            FolderCardSyncState.Scanning -> R.string.state_scanning
            FolderCardSyncState.UpToDate -> R.string.state_up_to_date
            FolderCardSyncState.Paused -> R.string.state_paused
        }
        Text(text = stringResource(id = stateLabel))

        if (folder.syncState is FolderCardSyncState.InProgress) {
            Box(
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                LinearProgressIndicator(
                    progress = { folder.syncState.progress },
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
private fun FolderCardUpToDatePreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState(
                syncState = FolderCardSyncState.UpToDate,
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}

@Preview
@Composable
private fun FolderCardErrorPreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState(
                syncState = FolderCardSyncState.Error,
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}

@Preview
@Composable
private fun FolderCardInProgressPreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState(
                syncState = FolderCardSyncState.InProgress(progress = 0.69f),
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}

@Preview
@Composable
private fun FolderCardScanningPreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState(
                syncState = FolderCardSyncState.Scanning,
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}

@Preview
@Composable
private fun FolderCardPausedPreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState(
                syncState = FolderCardSyncState.Paused,
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}