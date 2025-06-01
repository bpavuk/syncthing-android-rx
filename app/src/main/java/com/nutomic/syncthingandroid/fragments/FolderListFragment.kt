package com.nutomic.syncthingandroid.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.ListFragment
import com.nutomic.syncthingandroid.R
import com.nutomic.syncthingandroid.SyncthingApp
import com.nutomic.syncthingandroid.activities.FolderActivity
import com.nutomic.syncthingandroid.activities.MainActivity
import com.nutomic.syncthingandroid.activities.SyncthingActivity
import com.nutomic.syncthingandroid.model.Folder
import com.nutomic.syncthingandroid.service.AppPrefs
import com.nutomic.syncthingandroid.service.Constants
import com.nutomic.syncthingandroid.service.SyncthingService
import com.nutomic.syncthingandroid.service.SyncthingService.OnServiceStateChangeListener
import com.nutomic.syncthingandroid.util.ConfigRouter
import com.nutomic.syncthingandroid.util.ConfigXml.OpenConfigException
import com.nutomic.syncthingandroid.views.FoldersAdapter
import javax.inject.Inject

/**
 * Displays a list of all existing folders.
 */
class FolderListFragment : ListFragment(), OnServiceStateChangeListener, OnItemClickListener {
    private val ENABLE_DEBUG_LOG = false
    private var ENABLE_VERBOSE_LOG = false

    private var mConfigRouter: ConfigRouter? = null

    @JvmField
    @Inject
    var mPreferences: SharedPreferences? = null

    private val mUpdateListRunnable: Runnable = object : Runnable {
        override fun run() {
            onTimerEvent()
            mUpdateListHandler.postDelayed(this, Constants.GUI_UPDATE_INTERVAL)
        }
    }

    private val mUpdateListHandler = Handler()
    private var mLastVisibleToUser = false
    private var mAdapter: FoldersAdapter? = null
    private var mServiceState: SyncthingService.State? = SyncthingService.State.INIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as SyncthingApp).component().inject(this)
        ENABLE_VERBOSE_LOG = AppPrefs.getPrefVerboseLog(mPreferences)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // User switched to the current tab, start handler.
            startUpdateListHandler()
        } else {
            // User switched away to another tab, stop handler.
            stopUpdateListHandler()
        }
        mLastVisibleToUser = isVisibleToUser
    }

    override fun onPause() {
        stopUpdateListHandler()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mLastVisibleToUser) {
            startUpdateListHandler()
        }
    }

    private fun startUpdateListHandler() {
        LogV("startUpdateListHandler")
        mUpdateListHandler.removeCallbacks(mUpdateListRunnable)
        mUpdateListHandler.post(mUpdateListRunnable)
    }

    private fun stopUpdateListHandler() {
        LogV("stopUpdateListHandler")
        mUpdateListHandler.removeCallbacks(mUpdateListRunnable)
    }

    override fun onServiceStateChange(currentState: SyncthingService.State?) {
        mServiceState = currentState
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        setEmptyText(getString(R.string.folder_list_empty))
        getListView().onItemClickListener = this
    }

    /**
     * Invokes updateList which polls the REST API for folder status updates
     * while the user is looking at the current tab.
     */
    private fun onTimerEvent() {
        val mainActivity = activity as MainActivity?
        if (mainActivity == null) {
            return
        }
        if (mainActivity.isFinishing) {
            return
        }
        if (ENABLE_DEBUG_LOG) {
            LogV("Invoking updateList on UI thread")
        }
        mainActivity.runOnUiThread(Runnable { this@FolderListFragment.updateList() })
    }

    /**
     * Refreshes ListView by updating folders and info.
     *
     * Also creates adapter if it doesn't exist yet.
     */
    private fun updateList() {
        val activity = activity as SyncthingActivity?
        if (activity == null || view == null || activity.isFinishing) {
            return
        }
        if (mConfigRouter == null) {
            mConfigRouter = ConfigRouter(activity)
        }
        val folders: MutableList<Folder?>?
        val restApi = activity.api
        try {
            folders = mConfigRouter!!.getFolders(restApi)
        } catch (e: OpenConfigException) {
            Log.e(TAG, "Failed to parse existing config. You will need support from here ...")
            return
        }
        if (folders == null) {
            return
        }
        if (mAdapter == null) {
            mAdapter = FoldersAdapter(activity)
            setListAdapter(mAdapter)
        }
        mAdapter!!.setRestApi(restApi)

        // Prevent scroll position reset due to list update from clear().
        mAdapter!!.setNotifyOnChange(false)
        mAdapter!!.clear()
        mAdapter!!.addAll(folders)
        mAdapter!!.notifyDataSetChanged()
        setListShown(true)
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        val intent = Intent(activity, FolderActivity::class.java)
            .putExtra(FolderActivity.EXTRA_IS_CREATE, false)
            .putExtra(FolderActivity.EXTRA_FOLDER_ID, mAdapter!!.getItem(i)!!.id)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.folder_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if (itemId == R.id.add_folder) {
            val intent = Intent(activity, FolderActivity::class.java)
                .putExtra(FolderActivity.EXTRA_IS_CREATE, true)
            startActivity(intent)
            return true
        } else if (itemId == R.id.rescan_all) {
            rescanAll()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun rescanAll() {
        val activity = activity as SyncthingActivity?
        if (activity == null || view == null || activity.isFinishing) {
            return
        }
        val restApi = activity.api
        if (restApi == null || !restApi.isConfigLoaded) {
            Log.e(TAG, "rescanAll skipped because Syncthing is not running.")
            return
        }
        restApi.rescanAll()
    }

    private fun LogV(logMessage: String) {
        if (ENABLE_VERBOSE_LOG) {
            Log.v(TAG, logMessage)
        }
    }

    companion object {
        private const val TAG = "FolderListFragment"
    }
}
