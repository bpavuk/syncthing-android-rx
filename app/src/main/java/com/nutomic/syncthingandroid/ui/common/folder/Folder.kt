package com.nutomic.syncthingandroid.ui.common.folder

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
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
import com.nutomic.syncthingandroid.ui.common.progressBar.ProgressBar

data class FolderState(
    val state: FolderSyncState,
    val view: FolderCardDataView
)

sealed interface FolderSyncState {
    data object UpToDate : FolderSyncState
    data class InProgress(val progress: Float) : FolderSyncState
    data object Scanning : FolderSyncState
    data object Error : FolderSyncState
}

data class FolderCardDataView(
    val label: String,
    val path: String,
    val paused: Boolean
)

@Composable
fun FolderCard(
    folder: FolderState,
    modifier: Modifier = Modifier,
    onFolderCardClick: () -> Unit = {}
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (folder.state is FolderSyncState.Error) {
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
            .clickable(onClick = onFolderCardClick)
            .padding(16.dp) then modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.view.label,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = folder.view.path,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis
                )
            }
            Icon(
                Icons.Default.Folder,
                contentDescription = null
            )
        }

        val stateLabel = when (folder.state) {
            FolderSyncState.Error -> R.string.state_error
            is FolderSyncState.InProgress -> R.string.state_syncing_general
            FolderSyncState.Scanning -> R.string.state_scanning
            FolderSyncState.UpToDate -> R.string.state_up_to_date
        }
        Text(text = stringResource(id = stateLabel), )

        if (folder.state is FolderSyncState.InProgress) {
            ProgressBar(
                progress = folder.state.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
private fun FolderCardUpToDatePreview() {

}
