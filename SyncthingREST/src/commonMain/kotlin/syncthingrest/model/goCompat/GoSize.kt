package syncthingrest.model.goCompat

import kotlinx.serialization.Serializable

@Serializable
data class GoSize(
    val value: Int,
    val unit: String
)
