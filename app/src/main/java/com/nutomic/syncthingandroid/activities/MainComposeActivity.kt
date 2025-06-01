package com.nutomic.syncthingandroid.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nutomic.syncthingandroid.ui.SyncthingandroidApp
import com.nutomic.syncthingandroid.ui.theme.SyncthingandroidTheme

class MainComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyncthingandroidTheme {
                SyncthingandroidApp()
            }
        }
    }
}

