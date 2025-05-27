package com.nutomic.syncthingandroid.util.compose.scopedStorage

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

@RequiresApi(Build.VERSION_CODES.R)
class ScopedStorageContract : ActivityResultContract<String, ActivityResult>() {
    override fun createIntent(
        context: Context,
        input: String
    ): Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        setData(("package:$input").toUri())
    }

    override fun parseResult(
        resultCode: Int,
        intent: Intent?
    ): ActivityResult = ActivityResult(resultCode, data = intent)
}
