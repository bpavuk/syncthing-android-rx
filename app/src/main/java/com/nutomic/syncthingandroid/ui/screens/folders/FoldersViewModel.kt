package com.nutomic.syncthingandroid.ui.screens.folders

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.nutomic.syncthingandroid.activities.SyncthingActivity
import com.nutomic.syncthingandroid.model.Folder
import com.nutomic.syncthingandroid.util.ConfigRouter
import com.nutomic.syncthingandroid.util.ConfigXml

interface FoldersViewModel {
    val folders: List<Folder>

    fun retrieveFolders()
}

class FoldersViewModelImpl(
    application: SyncthingActivity
) : FoldersViewModel, AndroidViewModel(application.applicationContext as Application) {
    private val configRouter = ConfigRouter(application)
    private val restApi = application.api

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
