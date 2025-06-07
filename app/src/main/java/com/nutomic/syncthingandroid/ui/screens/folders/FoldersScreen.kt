package com.nutomic.syncthingandroid.ui.screens.folders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.ui.common.folder.FolderCard
import kotlinx.coroutines.delay

@Composable
fun FoldersScreen(
    viewModel: FoldersViewModel,
    modifier: Modifier = Modifier
) {
    val folders = viewModel.folders

    // TODO: once [SyncthingService] is rewritten with Coroutines and Flow, replace with
    //  flows-as-states
    LaunchedEffect(null) {
        while (true) {
            viewModel.retrieveFolders()
            delay(2000)
        }
    }

    Column(Modifier.padding(16.dp) then modifier) {
        for (folder in folders) {
            FolderCard(
                folder = folder,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
