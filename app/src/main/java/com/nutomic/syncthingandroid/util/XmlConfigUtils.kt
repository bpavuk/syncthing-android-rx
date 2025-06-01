package com.nutomic.syncthingandroid.util

import android.app.Application
import android.util.Log
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException

// TODO: merge with ConfigXml.java when it gets rewritten in Kotlin

const val TAG = "XmlConfigUtils"

// TODO: make this function suspend
fun parseableConfigExists(application: Application): Boolean {
    val configExists = Constants.getConfigFile(application).exists()
    if (!configExists) {
        return false
    }
    var configParseable = false
    val configParseTest = ConfigXml(application)
    try {
        configParseTest.loadConfig()
        configParseable = true
    } catch (_: OpenConfigException) {
        Log.d(TAG, "Failed to parse existing config. Will show key generation slide...")
    }
    return configParseable
}
