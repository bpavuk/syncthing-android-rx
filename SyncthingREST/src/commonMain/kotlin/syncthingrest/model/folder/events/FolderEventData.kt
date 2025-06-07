package syncthingrest.model.folder.events

import kotlinx.serialization.Serializable
import syncthingrest.model.events.EventData

@Serializable
sealed interface FolderEventData : EventData
