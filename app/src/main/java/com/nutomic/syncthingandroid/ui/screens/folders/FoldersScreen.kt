package com.nutomic.syncthingandroid.ui.screens.folders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    Column(modifier) {
        for (folder in folders) {
            Text(folder.label)
            folder.path?.let { Text(it) }
            Spacer(Modifier.size(8.dp))
        }
    }
}