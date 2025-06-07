package syncthingrest.model.xattr

import kotlinx.serialization.Serializable

@Serializable
data class XattrFilterEntry(
    val match: String,
    val permit: Boolean
)
