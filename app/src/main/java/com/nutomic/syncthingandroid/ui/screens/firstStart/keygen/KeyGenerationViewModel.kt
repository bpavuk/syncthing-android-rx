package com.nutomic.syncthingandroid.ui.screens.firstStart.keygen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import com.nutomic.syncthingandroid.service.SyncthingRunnable
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException
import com.nutomic.syncthingandroid.util.parseableConfigExists

interface KeyGenerationViewModel {
    val state: KeyGenerationState

    fun launchKeyGeneration()
}

class PreviewKeyGenerationViewModel(
    override val state: KeyGenerationState = KeyGenerationState.InProgress
) : KeyGenerationViewModel {
    override fun launchKeyGeneration() {}
}

class KeyGenerationViewModelImpl(application: Application) : AndroidViewModel(application),
    KeyGenerationViewModel {
    private val TAG = "KeyGenViewModel"
    override var state: KeyGenerationState by mutableStateOf(KeyGenerationState.InProgress)
        private set

    override fun launchKeyGeneration() {
        if (!parseableConfigExists(application)) {
            val configXml = ConfigXml(application)
            try {
                configXml.generateConfig()
            } catch (e: SyncthingRunnable.ExecutableNotFoundException) {
                Log.e(TAG, "Executable not found!", e)
                state = KeyGenerationState.ExecutableNotFound
                return
            } catch (e: OpenConfigException) {
                Log.e(TAG, "Failed to open config!", e)
                state = KeyGenerationState.Failed
                return
            }

            // Double-check!
            if (!parseableConfigExists(application)) {
                state = KeyGenerationState.Failed
            }
        }

        state = KeyGenerationState.Success
    }
}

sealed interface KeyGenerationState {
    data object Success : KeyGenerationState
    data object InProgress : KeyGenerationState
    data object ExecutableNotFound : KeyGenerationState
    data object Failed : KeyGenerationState
}
