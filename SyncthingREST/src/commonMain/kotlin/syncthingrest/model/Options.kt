package syncthingrest.model

import kotlinx.serialization.Serializable
import syncthingrest.model.goCompat.GoSize

@Serializable
data class Options(
    val listenAddresses: List<String> = listOf("default"),
    val globalAnnounceServers: List<String> = listOf("default"),
    val globalAnnounceEnabled: Boolean = true,
    val localAnnounceEnabled: Boolean = true,
    val localAnnouncePort: Int = 21027,
    val localAnnounceMCAddr: String = "[ff12::8384]:21027",
    val maxSendKbps: Int = 0, // No default in Go, 0 is Go's zero value
    val maxRecvKbps: Int = 0, // No default in Go, 0 is Go's zero value
    val reconnectionIntervalS: Int = 60,
    val relaysEnabled: Boolean = true,
    val relayReconnectIntervalM: Int = 10,
    val startBrowser: Boolean = true,
    val natEnabled: Boolean = true,
    val natLeaseMinutes: Int = 60,
    val natRenewalMinutes: Int = 30,
    val natTimeoutSeconds: Int = 10,
    val urAccepted: Int = 0, // No default in Go, 0 is Go's zero value
    val urSeen: Int = 0, // No default in Go, 0 is Go's zero value
    val urUniqueId: String = "", // No default in Go, generated if urAccepted > 0
    val urURL: String = "https://data.syncthing.net/newdata",
    val urPostInsecurely: Boolean = false,
    val urInitialDelayS: Int = 1800,
    val autoUpgradeIntervalH: Int = 12,
    val upgradeToPreReleases: Boolean = false, // No default in Go, false is Go's zero value
    val keepTemporariesH: Int = 24,
    val cacheIgnoredFiles: Boolean = false,
    val progressUpdateIntervalS: Int = 5,
    val limitBandwidthInLan: Boolean = false,
    val minHomeDiskFree: GoSize = GoSize(1, "%"), // Corresponds to Go's 'Size' type
    val releasesURL: String = "https://upgrades.syncthing.net/meta.json",
    val alwaysLocalNets: List<String> = emptyList(), // No default in Go, empty list is Go's zero value for slices
    val overwriteRemoteDeviceNamesOnConnect: Boolean = false,
    val tempIndexMinBlocks: Int = 10,
    val unackedNotificationIDs: List<String> = emptyList(), // No default in Go, empty list is Go's zero value for slices
    val trafficClass: Int = 0, // No default in Go, 0 is Go's zero value
    val maxFolderConcurrency: Int = 0, // Go's default logic applies if 0
    val crURL: String = "https://crash.syncthing.net/newcrash",
    val crashReportingEnabled: Boolean = true,
    val stunKeepaliveStartS: Int = 180,
    val stunKeepaliveMinS: Int = 20,
    val stunServers: List<String> = listOf("default"),
    val databaseTuning: String = "auto", // Corresponds to Go's 'Tuning' type, assuming string representation, default is empty string
    val maxConcurrentIncomingRequestKiB: Int = 0, // Go's default logic applies if 0
    val announceLANAddresses: Boolean = true,
    val sendFullIndexOnUpgrade: Boolean = false, // No default in Go, false is Go's zero value
    val featureFlags: List<String> = emptyList(), // No default in Go, empty list is Go's zero value for slices
    val auditEnabled: Boolean = false,
    val auditFile: String = "", // No default in Go, empty string is Go's zero value
    val connectionLimitEnough: Int = 0, // No default in Go, 0 is Go's zero value
    val connectionLimitMax: Int = 0, // No default in Go, 0 is Go's zero value
    val connectionPriorityTcpLan: Int = 10,
    val connectionPriorityQuicLan: Int = 20,
    val connectionPriorityTcpWan: Int = 30,
    val connectionPriorityQuicWan: Int = 40,
    val connectionPriorityRelay: Int = 50,
    val connectionPriorityUpgradeThreshold: Int = 0
)
