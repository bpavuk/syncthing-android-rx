package syncthingrest.model

import kotlinx.serialization.Serializable

@Serializable
data class Ignores(
    val lines: List<String> = emptyList()
)
