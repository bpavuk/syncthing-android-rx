package com.nutomic.syncthingandroid.activities

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nutomic.syncthingandroid.service.SyncthingService
import com.nutomic.syncthingandroid.service.SyncthingServiceBinder
import com.nutomic.syncthingandroid.ui.SyncthingandroidApp
import com.nutomic.syncthingandroid.ui.screens.devices.DevicesViewModelImpl
import com.nutomic.syncthingandroid.ui.screens.folders.FoldersViewModelImpl
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

val mainActivityModule = module {
    viewModel {
        FoldersViewModelImpl(
            configRouter = get()
        )
    }
    viewModel {
        DevicesViewModelImpl(
            application = get(),
            configRouter = get()
        )
    }
}

class MainComposeActivity : SyncthingActivity() {
    override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
        super.onServiceConnected(componentName, iBinder)
        val syncthingServiceBinder = iBinder as SyncthingServiceBinder
        val syncthingService = syncthingServiceBinder.service
        syncthingService.registerOnServiceStateChangeListener {
            when (it) {
                SyncthingService.State.INIT -> {
                    Log.d("MainComposeActivity", "Syncthing service: init")
                }
                SyncthingService.State.STARTING -> {
                    Log.d("MainComposeActivity", "Syncthing service: starting")
                }
                SyncthingService.State.ACTIVE -> {
                    Log.d("MainComposeActivity", "Syncthing service: active")
                }
                SyncthingService.State.DISABLED -> {
                    Log.d("MainComposeActivity", "Syncthing service: disabled")
                }
                SyncthingService.State.ERROR -> {
                    Log.d("MainComposeActivity", "Syncthing service: error")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, SyncthingService::class.java)
        // a weird workaround for starting a background service on Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        loadKoinModules(mainActivityModule)

        enableEdgeToEdge()
        setContent {
            SyncthingandroidTheme {
                SyncthingandroidApp()
            }
        }
    }
}
