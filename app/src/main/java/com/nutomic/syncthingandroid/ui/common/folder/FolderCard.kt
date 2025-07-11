package com.nutomic.syncthingandroid.ui.common.folder

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

sealed interface FolderCardState {
    data class Success(
        val syncState: FolderCardSyncState,
        val id: FolderID,
        val label: String,
        val path: String
    ) : FolderCardState

    data object Loading : FolderCardState
    data object Error : FolderCardState
}

sealed interface FolderCardSyncState {
    data object UpToDate : FolderCardSyncState
    data class InProgress(val progress: Float) : FolderCardSyncState
    data object Scanning : FolderCardSyncState
    data object Error : FolderCardSyncState
    data object Paused : FolderCardSyncState
}

@Composable
fun FolderCard(
    id: FolderID,
    viewModel: FolderCardViewModel,
    modifier: Modifier = Modifier,
    onFolderCardClick: () -> Unit = {}
) {
    LaunchedEffect(id, viewModel) {
        viewModel.updateFolder(id)
    }

    val state by viewModel.uiState.collectAsState()

    FolderCard(state, modifier, onFolderCardClick)
}

@Composable
fun FolderCard(
    folder: FolderCardState,
    modifier: Modifier = Modifier,
    onFolderCardClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (folder) {
            FolderCardState.Error -> MaterialTheme.colorScheme.errorContainer
            FolderCardState.Loading -> MaterialTheme.colorScheme.primaryContainer
            is FolderCardState.Success -> {
                when (folder.syncState) {
                    is FolderCardSyncState.Error -> MaterialTheme.colorScheme.errorContainer
                    is FolderCardSyncState.Paused -> yellowPrimaryContainer()
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
            }
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
        if (folder is FolderCardState.Success) {
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
                val animatedProgress by animateFloatAsState(
                    targetValue = folder.syncState.progress,
                    animationSpec = tween(),
                )
                Box(
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .height(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        } else {
            // TODO: create better UI for folder card loading
        }
    }
}

@Preview
@Composable
private fun FolderCardUpToDatePreview() {
    SyncthingandroidTheme {
        FolderCard(
            folder = FolderCardState.Success(
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
            folder = FolderCardState.Success(
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
            folder = FolderCardState.Success(
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
            folder = FolderCardState.Success(
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
            folder = FolderCardState.Success(
                syncState = FolderCardSyncState.Paused,
                label = "My Synced Folder",
                path = "/storage/emulated/0/Sync",
                id = FolderID("who-knows")
            )
        )
    }
}