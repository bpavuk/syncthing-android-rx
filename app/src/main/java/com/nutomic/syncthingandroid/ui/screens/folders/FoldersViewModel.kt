package com.nutomic.syncthingandroid.ui.screens.folders

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nutomic.syncthingandroid.model.Folder
import com.nutomic.syncthingandroid.service.RestApi
import com.nutomic.syncthingandroid.util.ConfigRouter
import com.nutomic.syncthingandroid.util.ConfigXml

interface FoldersViewModel {
    val folders: List<Folder>

    fun retrieveFolders()
}

class FoldersViewModelImpl(
    private val configRouter: ConfigRouter,
    private val restApi: RestApi
) : FoldersViewModel, ViewModel() {
    override var folders: List<Folder> by mutableStateOf(emptyList())
        private set

    override fun retrieveFolders() {
        try {
            folders = configRouter.getFolders(restApi)
        } catch (e: ConfigXml.OpenConfigException) {
            Log.e(
                TAG,
                "Failed to parse existing config. You will need support from here...",
                e
            )
        }
    }

    @Suppress("PrivatePropertyName")
    private val TAG = "FoldersViewModelImpl"
}
