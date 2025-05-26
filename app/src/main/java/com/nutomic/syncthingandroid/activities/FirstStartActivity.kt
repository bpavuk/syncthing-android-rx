package com.nutomic.syncthingandroid.activities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.SyncthingApp
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.service.SyncthingRunnable.ExecutableNotFoundException
import com.nutomic.syncthingandroid.util.ConfigXml
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException
import com.nutomic.syncthingandroid.util.PermissionUtil
import com.nutomic.syncthingandroid.util.Util
import com.nutomic.syncthingandroid.views.CustomViewPager
import java.lang.ref.WeakReference
import javax.inject.Inject

class FirstStartActivity : AppCompatActivity() {
    private class Slide(var layout: Int, var dotColorActive: Int, var dotColorInActive: Int)

    private lateinit var mSlides: Array<Slide?>

    /**
     * Initialize the slide's ViewPager position to "-1" so it will never
     * trigger any action in [.onBtnNextClick] if the slide is not
     * shown.
     */
    private var mSlidePosStoragePermission = -1
    private var mSlidePosIgnoreDozePermission = -1
    private var mSlideLocationPermission = -1
    private var mSlideNotificationPermission = -1
    private var mSlidePosKeyGeneration = -1

    private var mViewPager: CustomViewPager? = null
    private var mViewPagerAdapter: ViewPagerAdapter? = null
    private var mDotsLayout: LinearLayout? = null
    private lateinit var mDots: Array<TextView?>
    private var mBackButton: Button? = null
    private var mNextButton: Button? = null

    @JvmField
    @Inject
    var mPreferences: SharedPreferences? = null

    private var mRunningOnTV = false
    private var mUserDecisionIgnoreDozePermission = false

    /**
     * Handles activity behaviour depending on prerequisites.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as SyncthingApp).component().inject(this)

        mRunningOnTV = Util.isRunningOnTV(this)
        Log.d(TAG, if (mRunningOnTV) "Running on a TV Device" else "Running on a non-TV Device")

        /**
         * Check if prerequisites to run the app are still in place.
         * If anything mandatory is missing, the according welcome slide(s) will be shown.
         */
        val showSlideStoragePermission =
            !PermissionUtil.haveStoragePermission(this@FirstStartActivity)
        val showSlideIgnoreDozePermission = !haveIgnoreDozePermission()
        val showSlideLocationPermission = !haveLocationPermission()
        val showSlideNotificationPermission = !haveNotificationPermission()
        val showSlideKeyGeneration = !checkForParseableConfig()

        /**
         * If we don't have to show slides for mandatory prerequisites,
         * start directly into MainActivity.
         */
        if (!showSlideStoragePermission && !showSlideNotificationPermission && !showSlideKeyGeneration) {
            startApp()
            return
        }

        // Log what's missing and preventing us from directly starting into MainActivity.
        if (showSlideStoragePermission) {
            Log.d(TAG, "We (no longer?) have storage permission and will politely ask for it.")
        }
        if (showSlideIgnoreDozePermission) {
            Log.d(
                TAG,
                "We (no longer?) have ignore doze permission and will politely ask for it on phones."
            )
        }
        if (showSlideLocationPermission) {
            Log.d(TAG, "We (no longer?) have location permission and will politely ask for it.")
        }
        if (showSlideNotificationPermission) {
            Log.d(TAG, "We (no longer?) have notification permission and will politely ask for it.")
        }
        if (showSlideKeyGeneration) {
            Log.d(
                TAG,
                "We (no longer?) have a valid Syncthing config and will attempt to generate a fresh config."
            )
        }

        // Make notification bar transparent (API level 21+)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        // Show first start welcome wizard UI.
        setContentView(R.layout.activity_first_start)
        mViewPager = findViewById<View?>(R.id.view_pager) as CustomViewPager?
        mDotsLayout = findViewById<View?>(R.id.layoutDots) as LinearLayout
        mBackButton = findViewById<View?>(R.id.btn_back) as Button
        mNextButton = findViewById<View?>(R.id.btn_next) as Button?

        mViewPager!!.setPagingEnabled(false)

        // Add welcome slides to be shown.
        val colorsActive = getResources().getIntArray(R.array.array_dot_active)
        val colorsInactive = getResources().getIntArray(R.array.array_dot_inactive)
        var slideIndex = 0
        mSlides = arrayOfNulls<Slide>(
            1 +
                    (if (showSlideStoragePermission) 1 else 0) +
                    (if (showSlideIgnoreDozePermission) 1 else 0) +
                    (if (showSlideLocationPermission) 1 else 0) +
                    (if (showSlideNotificationPermission) 1 else 0) +
                    (if (showSlideKeyGeneration) 1 else 0)
        )
        mSlides[slideIndex++] =
            Slide(R.layout.activity_firststart_intro, colorsActive[0], colorsInactive[0])
        if (showSlideStoragePermission) {
            mSlidePosStoragePermission = slideIndex
            mSlides[slideIndex++] = Slide(
                R.layout.activity_firststart_storage_permission,
                colorsActive[1],
                colorsInactive[1]
            )
        }
        if (showSlideIgnoreDozePermission) {
            mSlidePosIgnoreDozePermission = slideIndex
            mSlides[slideIndex++] = Slide(
                R.layout.activity_firststart_ignore_doze_permission,
                colorsActive[4],
                colorsInactive[4]
            )
        }
        if (showSlideLocationPermission) {
            mSlideLocationPermission = slideIndex
            mSlides[slideIndex++] = Slide(
                R.layout.activity_firststart_location_permission,
                colorsActive[2],
                colorsInactive[2]
            )
        }
        if (showSlideNotificationPermission) {
            mSlideNotificationPermission = slideIndex
            mSlides[slideIndex++] = Slide(
                R.layout.activity_firststart_notification_permission,
                colorsActive[0],
                colorsInactive[0]
            )
        }
        if (showSlideKeyGeneration) {
            mSlidePosKeyGeneration = slideIndex
            mSlides[slideIndex++] = Slide(
                R.layout.activity_firststart_key_generation,
                colorsActive[3],
                colorsInactive[3]
            )
        }

        // Add bottom dots
        addBottomDots(0)

        // Make notification bar transparent
        changeStatusBarColor()

        mViewPagerAdapter = ViewPagerAdapter()
        mViewPager!!.setAdapter(mViewPagerAdapter)
        mViewPager!!.addOnPageChangeListener(mViewPagerPageChangeListener)

        mBackButton!!.setOnClickListener { onBtnBackClick() }

        mNextButton!!.setOnClickListener { onBtnNextClick() }

        if (mRunningOnTV) {
            mNextButton!!.setFocusableInTouchMode(true)
        }
        if (savedInstanceState != null) {
            mBackButton!!.visibility = if (savedInstanceState.getBoolean("mBackButton")) View.VISIBLE else View.GONE
            mNextButton!!.visibility = if (savedInstanceState.getBoolean("mNextButton")) View.VISIBLE else View.GONE
        }
        if (mNextButton!!.isVisible) {
            mNextButton!!.requestFocus()
        } else if (mBackButton!!.isVisible) {
            mBackButton!!.requestFocus()
        }
    }

    /**
     * Saves current tab index and fragment states.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean("mBackButton", mBackButton!!.isVisible)
        outState.putBoolean("mNextButton", mNextButton!!.isVisible)
    }

    public override fun onResume() {
        super.onResume()
        if (mNextButton == null || mViewPager == null) {
            return
        }
        if (mViewPager!!.currentItem == mSlidePosStoragePermission || mViewPager!!.currentItem == mSlideNotificationPermission || mViewPager!!.currentItem == mSlidePosIgnoreDozePermission) {
            mNextButton!!.performClick()
        }
    }

    fun onBtnBackClick() {
        val current = getItem(-1)
        if (current >= 0) {
            // Move to previous slider.
            mViewPager!!.setCurrentItem(current)
            if (current == 0) {
                mNextButton!!.requestFocus()
            }
        } else if (current == -1) {
            // "Back" on first slide quits the app.
            finish()
        }
    }

    fun onBtnNextClick() {
        // Check if we are allowed to advance to the next slide.
        if (mViewPager!!.currentItem == mSlidePosStoragePermission) {
            // As the storage permission is a prerequisite to run syncthing, refuse to continue without it.
            val storagePermissionsGranted =
                PermissionUtil.haveStoragePermission(this@FirstStartActivity)
            if (!storagePermissionsGranted) {
                Toast.makeText(
                    this, R.string.toast_write_storage_permission_required,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        if (mViewPager!!.currentItem == mSlidePosIgnoreDozePermission) {
            // As the ignore doze permission is a prerequisite to run Syncthing, refuse to continue without it.
            if (!haveIgnoreDozePermission()) {
                /**
                 * a) Phones, tablets: The ignore doze permission is recommended.
                 * b) TVs: The ignore doze permission is optional as it can only set by ADB on Android 8+.
                 */
                if (!mUserDecisionIgnoreDozePermission && !mRunningOnTV) {
                    AlertDialog.Builder(this@FirstStartActivity)
                        .setMessage(R.string.dialog_confirm_skip_ignore_doze_permission)
                        .setPositiveButton(
                            android.R.string.yes
                        ) { dialog: DialogInterface?, which: Int ->
                            mUserDecisionIgnoreDozePermission = true
                            onBtnNextClick()
                        }
                        .setNegativeButton(
                            android.R.string.no
                        ) { dialog: DialogInterface?, which: Int ->
                            mUserDecisionIgnoreDozePermission = false
                        }
                        .show()

                    // Case a) - Prevent user moving on with the slides.
                    return
                }
            }
        }

        if (mViewPager!!.currentItem == mSlideNotificationPermission) {
            // As the notification permission is a prerequisite to run the syncthing service permanently, refuse to continue without it.
            if (!haveNotificationPermission()) {
                Toast.makeText(
                    this, R.string.toast_notification_permission_required,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val current = getItem(+1)
        if (current < mSlides.size) {
            // Move to next slide.
            mViewPager!!.setCurrentItem(current)
            mBackButton!!.visibility = View.VISIBLE
            if (current == mSlidePosIgnoreDozePermission) {
                if (mRunningOnTV) {
                    /**
                     * Display workaround notice: Without workaround SyncthingNative can't run reliably on TV's running Android 8+.
                     * See issue https://github.com/Catfriend1/syncthing-android/issues/192
                     */
                    val ignoreDozeOsNotice =
                        findViewById<View?>(R.id.tvIgnoreDozePermissionOsNotice) as TextView
                    ignoreDozeOsNotice.text = getString(
                        R.string.ignore_doze_permission_os_notice,
                        getString(R.string.wiki_url),
                        "Android-TV-preparations"
                    )
                    ignoreDozeOsNotice.visibility = View.VISIBLE
                }
            } else if (current == mSlideLocationPermission) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val locationPermissionTipApi29 =
                        findViewById<View?>(R.id.locationPermissionTipApi29) as TextView
                    locationPermissionTipApi29.visibility = View.VISIBLE
                }
            } else if (current == mSlidePosKeyGeneration) {
                onKeyGenerationSlideShown()
            }
        } else {
            // Start the app after "mNextButton" was hit on the last slide.
            Log.v(TAG, "User completed first start UI.")
            startApp()
        }
    }

    private fun addBottomDots(currentPage: Int) {
        mDots = arrayOfNulls(mSlides.size)

        mDotsLayout!!.removeAllViews()
        for (i in mDots.indices) {
            mDots[i] = TextView(this)
            mDots[i]!!.text = Html.fromHtml("&#8226;")
            mDots[i]!!.textSize = 35f
            mDots[i]!!.setTextColor(mSlides[currentPage]!!.dotColorInActive)

            // Prevent TalkBack from announcing a decorative TextView.
            mDots[i]!!.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO)
            mDots[i]!!.setContentDescription(
                getString(
                    R.string.page_x_of_y,
                    i.toString(),
                    mDots.size.toString()
                )
            )
            mDotsLayout!!.addView(mDots[i])
        }

        if (mDots.isNotEmpty()) mDots[currentPage]!!.setTextColor(mSlides[currentPage]!!.dotColorActive)
    }

    private fun getItem(i: Int): Int {
        return mViewPager!!.currentItem + i
    }

    //  ViewPager change listener
    var mViewPagerPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            addBottomDots(position)

            // Change the next button text from next to finish on last slide.
            mNextButton!!.text = getString(if (position == mSlides.size - 1) R.string.finish else R.string.cont)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
        }

        override fun onPageScrollStateChanged(arg0: Int) {
        }
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            val window = getWindow()
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * View pager adapter
     */
    inner class ViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater!!.inflate(mSlides[position]!!.layout, container, false)

            /* Slide: storage permission */
            val btnGrantStoragePerm = view.findViewById<View?>(R.id.btnGrantStoragePerm) as Button?
            btnGrantStoragePerm?.setOnClickListener {
                PermissionUtil.requestStoragePermission(
                    this@FirstStartActivity,
                    REQUEST_WRITE_STORAGE
                )
            }

            /* Slide: ignore doze permission */
            val btnGrantIgnoreDozePerm =
                view.findViewById<View?>(R.id.btnGrantIgnoreDozePerm) as Button?
            btnGrantIgnoreDozePerm?.setOnClickListener { requestIgnoreDozePermission() }

            /* Slide: location permission */
            val btnGrantLocationPerm =
                view.findViewById<View?>(R.id.btnGrantLocationPerm) as Button?
            btnGrantLocationPerm?.setOnClickListener { requestLocationPermission() }

            /* Slide: notification permission */
            val btnGrantNotificationPerm =
                view.findViewById<View?>(R.id.btnGrantNotificationPerm) as Button?
            btnGrantNotificationPerm?.setOnClickListener { requestNotificationPermission() }

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return mSlides.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    /**
     * Preconditions:
     * Storage permission has been granted.
     */
    private fun startApp() {
        val mainIntent = Intent(this, MainActivity::class.java)
        /**
         * In case start_into_web_gui option is enabled, start both activities
         * so that back navigation works as expected.
         */
        if (mPreferences!!.getBoolean(Constants.PREF_START_INTO_WEB_GUI, false)) {
            startActivities(arrayOf(mainIntent, Intent(this, WebGuiActivity::class.java)))
        } else {
            startActivity(mainIntent)
        }
        finish()
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
    @TargetApi(23)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_COARSE_LOCATION -> if (grantResults.isEmpty() ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "User denied ACCESS_COARSE_LOCATION permission.")
            } else {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "User granted ACCESS_COARSE_LOCATION permission.")
                mNextButton!!.requestFocus()
                onBtnNextClick()
            }

            REQUEST_BACKGROUND_LOCATION -> if (grantResults.isEmpty() ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "User denied ACCESS_BACKGROUND_LOCATION permission.")
            } else {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "User granted ACCESS_BACKGROUND_LOCATION permission.")
                mNextButton!!.requestFocus()
                onBtnNextClick()
            }

            REQUEST_FINE_LOCATION -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(TAG, "User denied ACCESS_FINE_LOCATION permission.")
                    return
                }
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "User granted ACCESS_FINE_LOCATION permission.")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        REQUEST_BACKGROUND_LOCATION
                    )
                    return
                }
            }

            REQUEST_WRITE_STORAGE -> if (grantResults.isEmpty() ||
                grantResults[0] != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "User denied WRITE_EXTERNAL_STORAGE permission.")
            } else {
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "User granted WRITE_EXTERNAL_STORAGE permission.")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    mNextButton!!.requestFocus()
                    return
                }
                onBtnNextClick()
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    /**
     * Perform secure key generation in an AsyncTask.
     */
    private fun onKeyGenerationSlideShown() {
        mBackButton!!.visibility = View.GONE
        mNextButton!!.visibility = View.GONE
        val keyGenerationTask = KeyGenerationTask(this)
        keyGenerationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
    }

    /**
     * Sets up the initial configuration and generates secure keys.
     */
    private class KeyGenerationTask(context: FirstStartActivity?) :
        AsyncTask<Void?, String?, Void?>() {
        private val refFirstStartActivity: WeakReference<FirstStartActivity?> = WeakReference<FirstStartActivity?>(context)
        var configXml: ConfigXml? = null

        override fun doInBackground(vararg voids: Void?): Void? {
            val firstStartActivity = refFirstStartActivity.get()
            if (firstStartActivity == null) {
                cancel(true)
                return null
            }
            configXml = ConfigXml(firstStartActivity)
            try {
                // Create new secure keys and config.
                configXml!!.generateConfig()
            } catch (e: ExecutableNotFoundException) {
                publishProgress(
                    firstStartActivity.getString(
                        R.string.executable_not_found,
                        e.message
                    )
                )
                cancel(true)
            } catch (e: OpenConfigException) {
                publishProgress(firstStartActivity.getString(R.string.config_create_failed))
                cancel(true)
            }
            return null
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            if (values.isNotEmpty()) {
                val firstStartActivity = refFirstStartActivity.get()
                if (firstStartActivity == null) {
                    return
                }
                val keygenStatus =
                    firstStartActivity.findViewById<TextView>(R.id.key_generation_status)
                keygenStatus.text = values[0]
            }
        }

        override fun onPostExecute(aVoid: Void?) {
            // Get a reference to the activity if it is still there.
            val firstStartActivity = refFirstStartActivity.get()
            if (firstStartActivity == null) {
                return
            }
            val keygenStatus =
                firstStartActivity.findViewById<View?>(R.id.key_generation_status) as TextView
            if (!firstStartActivity.checkForParseableConfig()) {
                keygenStatus.text = firstStartActivity.getString(R.string.config_read_failed)
                return
            }
            keygenStatus.text = firstStartActivity.getString(R.string.key_generation_success)
            val nextButton = firstStartActivity.findViewById<View?>(R.id.btn_next) as Button
            nextButton.visibility = View.VISIBLE
            nextButton.requestFocus()
            nextButton.performClick()
        }
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
        private const val TAG = "FirstStartActivity"
        private const val REQUEST_COARSE_LOCATION = 141
        private const val REQUEST_BACKGROUND_LOCATION = 142
        private const val REQUEST_FINE_LOCATION = 144
        private const val REQUEST_NOTIFICATION = 145
        private const val REQUEST_WRITE_STORAGE = 143
    }
}
