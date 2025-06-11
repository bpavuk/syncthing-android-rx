package com.nutomic.syncthingandroid.ui.screens.folders

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutomic.syncthingandroid.ui.common.folder.FolderCard
import org.koin.androidx.compose.koinViewModel
import syncthingrest.model.folder.FolderID

@Composable
fun FolderListScreen(
    viewModel: FolderListViewModel,
    modifier: Modifier = Modifier
) {
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
            LaunchedEffect(viewModel) {
                viewModel.updateFolders()
            }
            val uiState = viewModel.uiState.collectAsState().value

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .scrollable(
                        state = rememberScrollState(),
                        orientation = Orientation.Vertical
                    ) then modifier
            ) {
                when (uiState) {
                    FolderListUIState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize()
                        )
                    }

                    FolderListUIState.FailedToLoad -> {
                        Text("Failed to load folders. You may need support at this point...")
                    }

                    is FolderListUIState.Success -> {
                        if (uiState.folderCards.isEmpty()) {
                            Text("No folders found.")
                        } else {
                            for (folder in uiState.folderCards) {
                                FolderCard(
                                    folder = folder,
                                    modifier = Modifier.fillMaxWidth(),
                                    onFolderCardClick = {
                                        currentDestination = FolderDestinations.Folder(folder.id)
                                    }
                                )
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed interface FolderDestinations {
    data object Home : FolderDestinations
    data class Folder(val folderID: FolderID) : FolderDestinations
}
