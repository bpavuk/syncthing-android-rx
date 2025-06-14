package com.nutomic.syncthingandroid.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

class LicenseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LicenseScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen() {
    SyncthingandroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
            val context = LocalContext.current
            val libraries by rememberLibraries {
                val inputStream = context.resources.openRawResource(R.raw.aboutlibraries)
                inputStream.bufferedReader().use { it.readText() }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(id = R.string.open_source_licenses_title)) },
                        navigationIcon = {
                            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(id = R.string.back)
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                LibrariesContainer(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    libraries = libraries
                )
            }
        }
    }
}
