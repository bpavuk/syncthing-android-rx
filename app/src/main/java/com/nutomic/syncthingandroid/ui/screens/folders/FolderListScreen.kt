package com.nutomic.syncthingandroid.ui.screens.folders

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.ui.common.folder.FolderCard
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import syncthingrest.model.folder.FolderID

@Composable
fun FolderListScreen(
    viewModel: FolderListViewModel,
    modifier: Modifier = Modifier
) {
    val folders = viewModel.folders
    var currentDestination: FolderDestinations by remember {
        mutableStateOf(FolderDestinations.Home)
    }

    when (currentDestination) {
        is FolderDestinations.Folder -> {
            BackHandler {
                currentDestination = FolderDestinations.Home
            }

            FolderSettingsScreen(
                viewModel = koinViewModel<FolderSettingsViewModelImpl>(),
                folderId = (currentDestination as FolderDestinations.Folder).folderID,
                modifier = Modifier.padding(16.dp) then modifier
            )
        }
        FolderDestinations.Home -> {
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
                        modifier = Modifier.fillMaxWidth(),
                        onFolderCardClick = {
                            currentDestination = FolderDestinations.Folder(folder.view.id)
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

sealed interface FolderDestinations {
    data object Home : FolderDestinations
    data class Folder(val folderID: FolderID) : FolderDestinations
}
