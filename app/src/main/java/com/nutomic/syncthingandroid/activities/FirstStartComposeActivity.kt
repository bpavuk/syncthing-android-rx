package com.nutomic.syncthingandroid.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.ui.screens.FirstStartScreen
import com.nutomic.syncthingandroid.ui.screens.Slide
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException

class FirstStartComposeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showSlideLocationPermission = !haveLocationPermission()
        val showSlideNotificationPermission = !haveNotificationPermission()
        val showSlideKeyGeneration = !checkForParseableConfig()
        val slides = mutableListOf<Slide>()

        slides.add(Slide.Welcome)
        slides.add(Slide.StoragePermission)
        slides.add(Slide.IgnoreDozePermission)
        slides.add(Slide.LocationPermission)
        slides.add(Slide.NotificationPermission)
        if (showSlideKeyGeneration) slides.add(Slide.KeyGeneration)

        enableEdgeToEdge()
        setContent {
            SyncthingandroidTheme {
                FirstStartScreen(
                    modifier = Modifier.fillMaxSize(),
                    slides = slides,
                    onIntroFinished = {}
                )
            }
        }
    }

    private fun haveLocationPermission(): Boolean {
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        var backgroundLocationGranted = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        return coarseLocationGranted && backgroundLocationGranted
    }

    private fun requestLocationPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_FINE_LOCATION
                )
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUND_LOCATION
                )
            }
            else -> ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_COARSE_LOCATION
            )
        }
    }

    private fun haveNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            REQUEST_NOTIFICATION
        )
    }

    private fun checkForParseableConfig(): Boolean {
        /**
         * Check if a valid config exists that can be read and parsed.
         */
        val configExists = Constants.getConfigFile(this).exists()
        if (!configExists) {
            return false
        }
        var configParseable = false
        val configParseTest = ConfigXml(this)
        try {
            configParseTest.loadConfig()
            configParseable = true
        } catch (e: OpenConfigException) {
            Log.d(TAG, "Failed to parse existing config. Will show key generation slide ...")
        }
        return configParseable
    }

    companion object {
        private const val TAG = "FirstStartCompose"
        private const val REQUEST_COARSE_LOCATION = 141
        private const val REQUEST_BACKGROUND_LOCATION = 142
        private const val REQUEST_FINE_LOCATION = 144
        private const val REQUEST_NOTIFICATION = 145
    }
}
