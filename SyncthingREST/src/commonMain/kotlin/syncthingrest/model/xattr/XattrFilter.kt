package syncthingrest.model.xattr

import kotlinx.serialization.Serializable

@Serializable
data class XattrFilter(
    val entries: List<XattrFilterEntry> = emptyList(),
    val maxSingleEntrySize: Int = 1024,
    val maxTotalSize: Int = 4096
)
