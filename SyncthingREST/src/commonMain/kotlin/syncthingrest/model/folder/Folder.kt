package syncthingrest.model.folder

import kotlinx.serialization.Serializable
import syncthingrest.model.device.SharedWithDevice
import syncthingrest.model.xattr.XattrFilter

const val FILENAME_STFOLDER = ".stfolder"

/**
 * Sources:
 * - https://github.com/syncthing/syncthing/tree/master/lib/config
 * - https://github.com/syncthing/syncthing/blob/master/lib/config/folderconfiguration.go
 */
@Serializable
data class Folder(
    // Folder Configuration
    var id: String,
    var label: String = "",
    var filesystemType: String = "basic",
    var path: String,
    var type: FolderType = FolderType.SEND_RECEIVE,
    val devices: List<SharedWithDevice> = emptyList(),
    var fsWatcherEnabled: Boolean = true,
    var fsWatcherDelayS: Float = 10f,
    var fsWatcherTimeoutS: Float = 0f,
    val sharedWithDevices: MutableList<SharedWithDevice> = mutableListOf(),

    /**
     * Folder rescan interval defaults to 3600s as it is the default in
     * syncthing when the file watcher is enabled and a new folder is created.
     */
    var rescanIntervalS: Int = 3600,
    var ignorePerms: Boolean = true,
    var autoNormalize: Boolean = true,
    var minDiskFree: MinDiskFree? = null,
    var versioning: Versioning? = null,
    var copiers: Int = 0,
    var pullerMaxPendingKiB: Int = 0,
    var hashers: Int = 0,
    var order: String = "random",
    var ignoreDelete: Boolean = false,
    var scanProgressIntervalS: Int = 0,
    var pullerPauseS: Int = 0,
    var maxConflicts: Int = 10,
    var disableSparseFiles: Boolean = false,
    var disableTempIndexes: Boolean = false,
    var paused: Boolean = false,
    var weakHashThresholdPct: Int = 25,
    var markerName: String = FILENAME_STFOLDER,

    // Since v1.1.0, see Issue #5445, PR #5479
    var copyOwnershipFromParent: Boolean = false,

    // Since v1.2.1, see PR #5852
    var modTimeWindowS: Int = 0,

    // Since v1.6.0
    // see PR #6587: "inorder", "random", "standard"
    var blockPullOrder: String = "standard",

    // see PR #6588
    var disableFsync: Boolean = false,

    // see PR #6573
    var maxConcurrentWrites: Int = 2,

    // Since v1.8.0
    // see PR #6746: "all", "copy_file_range", "duplicate_extents", "ioctl", "sendfile", "standard"
    var copyRangeMethod: String = "standard",

    // Since v1.9.0
    // see https://github.com/syncthing/syncthing/commit/932d8c69de9e34824ecc4d5de0a482dfdb71936e
    var caseSensitiveFS: Boolean = false,
    var junctionsAsDirs: Boolean = false,

    // Since v1.21.0
    var syncOwnership: Boolean = false,
    var sendOwnership: Boolean = false,

    // Since v1.22.0
    var syncXattrs: Boolean = false,
    var sendXattrs: Boolean = false,

    var xattrFilter: XattrFilter = XattrFilter(),

    // Folder Status
    var invalid: String? = null,
) {
    @Serializable
    data class Versioning(
        var type: String? = null,
        var cleanupIntervalS: Int = 0,
        var params: MutableMap<String?, String?> = HashMap(),

        // Since v1.14.0
        var fsPath: String? = null,
        var fsType: String = "basic", // default: "basic"
    )

    @Serializable
    data class MinDiskFree(
        var value: Float = 1f,
        var unit: String = "%",
    )
}