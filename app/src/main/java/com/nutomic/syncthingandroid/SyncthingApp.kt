package com.nutomic.syncthingandroid

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.nutomic.syncthingandroid.service.RestApiKt
import com.nutomic.syncthingandroid.util.ConfigRouter
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import javax.inject.Inject

// Define your Koin modules here or in a separate file
val appModule = module {
    single { ConfigRouter(androidApplication()) }
    single { RestApiKt() }
}

class SyncthingApp : Application() {
    @Inject
    lateinit var mComponent: DaggerComponent

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin
        startKoin {
            androidContext(this@SyncthingApp)
            modules(appModule)
        }

        DaggerDaggerComponent.builder()
            .syncthingModule(SyncthingModule(this))
            .build()
            .inject(this)

        // Set VM policy to avoid crash when sending folder URI to file manager.
        val vmPolicy = VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setVmPolicy(vmPolicy)

        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)
        {
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
            StrictMode.setThreadPolicy(threadPolicy);
        }
        */
    }

    fun component(): DaggerComponent {
        return mComponent
    }
}
