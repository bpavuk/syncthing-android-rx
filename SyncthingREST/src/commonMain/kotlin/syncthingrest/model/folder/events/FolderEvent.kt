package syncthingrest.model.folder.events

import kotlinx.serialization.Serializable
import syncthingrest.model.events.Event

@Serializable
sealed interface FolderEvent : Event<FolderEventData> {
    override val id: Int
    override val globalID: Int
    override val type: String
    override val time: String
    override val data: FolderEventData
}
