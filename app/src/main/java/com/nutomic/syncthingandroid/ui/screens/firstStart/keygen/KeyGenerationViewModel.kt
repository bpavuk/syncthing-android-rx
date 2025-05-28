package com.nutomic.syncthingandroid.ui.screens.firstStart.keygen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.service.SyncthingRunnable
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            if (shouldGenerateKey().await()) {
                val configXml = ConfigXml(application)
                try {
                    configXml.generateConfig()
                } catch (e: SyncthingRunnable.ExecutableNotFoundException) {
                    Log.e(TAG, "Executable not found!", e)
                    state = KeyGenerationState.ExecutableNotFound
                    return@launch
                } catch (e: OpenConfigException) {
                    Log.e(TAG, "Failed to open config!", e)
                    state = KeyGenerationState.Failed
                    return@launch
                }
            }

            state = KeyGenerationState.Success
        }
    }

    fun shouldGenerateKey(): Deferred<Boolean> =
        viewModelScope.async {
            val configExists = Constants.getConfigFile(application).exists()
            if (!configExists) {
                return@async false
            }
            var configParseable = false
            val configParseTest = ConfigXml(application)
            try {
                configParseTest.loadConfig()
                configParseable = true
            } catch (_: OpenConfigException) {
                Log.d(TAG, "Failed to parse existing config. Will show key generation slide...")
            }
            return@async configParseable
        }

}

sealed interface KeyGenerationState {
    data object Success : KeyGenerationState
    data object InProgress : KeyGenerationState
    data object ExecutableNotFound : KeyGenerationState
    data object Failed : KeyGenerationState
}
