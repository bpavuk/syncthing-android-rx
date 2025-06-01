package com.nutomic.syncthingandroid.ui

import androidx.activity.compose.LocalActivity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.activities.SyncthingActivity
import com.nutomic.syncthingandroid.ui.screens.folders.FoldersScreen
import com.nutomic.syncthingandroid.ui.screens.folders.FoldersViewModelImpl

@PreviewScreenSizes
@Composable
fun SyncthingandroidApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.FOLDERS) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = stringResource(it.label)
                        )
                    },
                    label = { Text(stringResource(it.label)) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            when (currentDestination) {
                AppDestinations.FOLDERS -> FoldersScreen(
                    modifier = Modifier.padding(innerPadding),
                    viewModel = FoldersViewModelImpl(LocalActivity.current as SyncthingActivity)
                )
//                AppDestinations.DEVICES -> DevicesScreen(modifier = Modifier.padding(innerPadding))
//                AppDestinations.STATUS -> StatusScreen(modifier = Modifier.padding(innerPadding))
                else -> Text("TODO")
            }
        }
    }
}

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    FOLDERS(R.string.folders_fragment_title, Icons.Default.Home),
    DEVICES(R.string.devices_fragment_title, Icons.Default.Favorite),
    STATUS(R.string.status_fragment_title, Icons.Default.AccountBox),
}
