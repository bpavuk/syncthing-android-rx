package com.nutomic.syncthingandroid.util.configkt

import android.content.Context
import com.nutomic.syncthingandroid.util.ConfigXml
import syncthingrest.RestApiKt

class ConfigRouterKt(context: Context, val restApi: RestApiKt) {
    val configXml = ConfigXml(context)

    val devices = DeviceRouter(this)
    val folders = FolderRouter(this)
}
