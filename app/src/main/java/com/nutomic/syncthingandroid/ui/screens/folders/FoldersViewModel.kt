package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nutomic.syncthingandroid.util.ConfigRouterKt
import com.nutomic.syncthingandroid.util.ConfigXml
import syncthingrest.model.folder.Folder

interface FoldersViewModel {
    val folders: List<Folder>

    suspend fun retrieveFolders()
}

class FoldersViewModelImpl(
    private val configRouter: ConfigRouterKt,
) : FoldersViewModel, ViewModel() { // Changed to use application directly

    override var folders: List<Folder> by mutableStateOf(emptyList())
        private set

    override suspend fun retrieveFolders() {
        try {
            folders = configRouter.getFolders()
        } catch (e: ConfigXml.OpenConfigException) {
            Log.e(
                TAG,
                "Failed to parse existing config. You will need support from here...",
                e
            )
            folders = emptyList() // Ensure folders is in a consistent state on error
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "FoldersViewModelImpl"
}
