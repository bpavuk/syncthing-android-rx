package com.nutomic.syncthingandroid.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.ui.common.Dots
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException
import com.nutomic.syncthingandroid.util.PermissionUtil
import com.nutomic.syncthingandroid.util.compose.rememberScopedStoragePermissionState

class FirstStartComposeActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val showSlideStoragePermission =
            !PermissionUtil.haveStoragePermission(this@FirstStartComposeActivity)
        val showSlideIgnoreDozePermission = !haveIgnoreDozePermission()
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
                    slides = slides
                )
            }
        }
    }

    private fun haveIgnoreDozePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Older android version don't have the doze feature so we'll assume having the anti-doze permission.
            return true
        }
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    @SuppressLint("InlinedApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestIgnoreDozePermission() {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.setData("package:$packageName".toUri())
        try {
            val componentName = intent.resolveActivity(packageManager)
            if (componentName != null) {
                val className = componentName.className
                if (!className.equals(
                        "com.android.tv.settings.EmptyStubActivity",
                        ignoreCase = true
                    )
                ) {
                    // Launch "Exempt from doze mode?" dialog.
                    startActivity(intent)
                    return
                }
            } else {
                Log.w(TAG, "Request ignore battery optimizations not supported")
            }
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Request ignore battery optimizations not supported", e)
        }

        Toast.makeText(
            this,
            R.string.dialog_disable_battery_optimizations_not_supported,
            Toast.LENGTH_LONG
        ).show()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_FINE_LOCATION
            )
            return
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                REQUEST_BACKGROUND_LOCATION
            )
            return
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_COARSE_LOCATION
        )
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

sealed interface Slide {
    data object Welcome : Slide
    data object StoragePermission : Slide
    data object IgnoreDozePermission : Slide
    data object LocationPermission : Slide
    data object NotificationPermission : Slide
    data object KeyGeneration : Slide
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FirstStartScreen(
    slides: List<Slide>,
    modifier: Modifier = Modifier
) {
    // TODO: migrate to Nav3 when it's out of Alpha
    val slideCount = slides.count()
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = modifier,
        bottomBar = {
            SlidesController(
                modifier = Modifier.fillMaxWidth(),
                backHandler = if (currentSlideIndex <= 0) {
                    null
                } else {
                    {
                        currentSlideIndex -= 1
                    }
                },
                forwardHandler = {
                    when (slides[currentSlideIndex]) {
                        Slide.IgnoreDozePermission -> TODO()
                        Slide.KeyGeneration -> TODO()
                        Slide.LocationPermission -> TODO()
                        Slide.NotificationPermission -> TODO()
                        Slide.StoragePermission -> {

                        }
                        Slide.Welcome -> {
                            currentSlideIndex++
                        }
                    }
                },
                activeSlideNumber = currentSlideIndex + 1,
                slideCount = slideCount
            )
        }
    ) { innerPadding ->
        when (slides[currentSlideIndex]) {
            Slide.Welcome -> WelcomeSlide(Modifier.padding(innerPadding))
            Slide.StoragePermission -> StoragePermissionSlide(Modifier.padding(innerPadding))
            Slide.IgnoreDozePermission -> IgnoreDozePermissionSlide(Modifier.padding(innerPadding))
            Slide.LocationPermission -> LocationPermissionSlide(Modifier.padding(innerPadding))
            Slide.NotificationPermission -> NotificationPermissionSlide(
                Modifier.padding(
                    innerPadding
                )
            )

            Slide.KeyGeneration -> KeyGenerationSlide(Modifier.padding(innerPadding))
        }
    }
}

@Preview
@Composable
private fun FirstStartPreview() {
    SyncthingandroidTheme {
        FirstStartScreen(
            slides = listOf(Slide.Welcome, Slide.StoragePermission, Slide.LocationPermission),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun WelcomeSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TODO: make it more welcome.
        Text("Welcome!")
    }
}

@Preview
@Composable
private fun WelcomeSlidePreview() {
    SyncthingandroidTheme {
        WelcomeSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermissionSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            // Let's set up your storage
        }
        Column(
            modifier = Modifier.weight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                // handle pre-Scoped Storage permissions
                val storagePermissionState = rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (storagePermissionState.status.isGranted) {
                    // All set!
                    Text("All set!")
                } else {
                    // TODO: use string res
                    val text = if (storagePermissionState.status.shouldShowRationale) {
                        "Storage permission is required for Syncthing to function. Otherwise, we " +
                                "can't even sync a thing!"
                    } else {
                        "Please, grant the permission for storage access. With that permission, we " +
                                "will be able to sync your things."
                    }
                    Text(text)
                    Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            } else {
                // handle Scoped Storage
                val storagePermissionState = rememberScopedStoragePermissionState()

                Text("Let's set up Scoped Storage permissions!")
                Text(storagePermissionState.granted.toString())
                if (!storagePermissionState.granted) {
                    Button(onClick = { storagePermissionState.requestPermission() }) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StoragePermissionPreview() {
    SyncthingandroidTheme {
        StoragePermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun IgnoreDozePermissionSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview
@Composable
private fun IgnoreDozePermissionPreview() {
    SyncthingandroidTheme {
        IgnoreDozePermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LocationPermissionSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview
@Composable
private fun LocationPermissionPreview() {
    SyncthingandroidTheme {
        LocationPermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun NotificationPermissionSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Preview
@Composable
private fun NotificationPermissionPreview() {
    SyncthingandroidTheme {
        NotificationPermissionSlide(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun KeyGenerationSlide(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer) then modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}

@Composable
fun SlidesController(
    modifier: Modifier = Modifier,
    backHandler: (() -> Unit)? = null,
    forwardHandler: (() -> Unit)? = null,
    activeSlideNumber: Int,
    slideCount: Int
) {
    Box(modifier = modifier) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                backHandler ?: {},
                enabled = backHandler != null
            ) {
                Text(stringResource(R.string.back))
            }
            Button(
                forwardHandler ?: {},
                enabled = forwardHandler != null
            ) {
                Text(stringResource(R.string.cont))
            }
        }
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Dots(activeDot = activeSlideNumber, dotAmount = slideCount)
            }
        }
    }
}
