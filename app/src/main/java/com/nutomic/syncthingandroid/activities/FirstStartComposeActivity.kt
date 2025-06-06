package com.nutomic.syncthingandroid.activities

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.ui.screens.firstStart.FirstStartScreen
import com.nutomic.syncthingandroid.ui.screens.firstStart.Slide
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import com.nutomic.syncthingandroid.util.compose.doze.rememberDozePermissionState
import com.nutomic.syncthingandroid.util.compose.scopedStorage.rememberScopedStoragePermissionState
import com.nutomic.syncthingandroid.util.parseableConfigExists
import javax.inject.Inject

class FirstStartComposeActivity : AppCompatActivity() {
    @Inject
    @JvmField
    var mPreferences: SharedPreferences? = null // TODO: replace with Hilt/Koin

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val slides = mutableListOf<Slide>()

        slides.add(Slide.Welcome)
        slides.add(Slide.StoragePermission)
        slides.add(Slide.IgnoreDozePermission)
        slides.add(Slide.LocationPermission)
        slides.add(Slide.NotificationPermission)
        slides.add(Slide.KeyGeneration)

        enableEdgeToEdge()
        setContent {
            val isStorageGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                rememberScopedStoragePermissionState().granted
            } else {
                rememberPermissionState(
                    permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).status.isGranted
            }
            val isDozeGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rememberDozePermissionState().granted
            } else {
                true // assume true for earlier Android versions where Doze is non-existent
            }
            val isNotificationPermissionGranted =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberPermissionState(
                        permission = Manifest.permission.POST_NOTIFICATIONS
                    ).status.isGranted
                } else {
                    true // not needed on earlier Android versions
                }
            val isKeyConfigValid = parseableConfigExists(application)

            val shouldSkip =
                isStorageGranted && isDozeGranted && isNotificationPermissionGranted && isKeyConfigValid

            if (shouldSkip) {
                startApp()
                finish()
            }

            SyncthingandroidTheme {
                FirstStartScreen(
                    modifier = Modifier.fillMaxSize(), slides = slides, onIntroFinished = ::startApp
                )
            }
        }
    }


    private fun startApp() {
        val mainIntent = Intent(this, MainComposeActivity::class.java)
        /**
         * In case start_into_web_gui option is enabled, start both activities
         * so that back navigation works as expected.
         */
        if (mPreferences?.getBoolean(Constants.PREF_START_INTO_WEB_GUI, false) == true) {
            startActivities(arrayOf(mainIntent, Intent(this, WebGuiActivity::class.java)))
        } else {
            startActivity(mainIntent)
        }
        finish()
    }

    companion object {
        private const val TAG = "FirstStartCompose"
    }
}
