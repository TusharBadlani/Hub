package cessini.technology.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "LiveRooms"
)
data class LiveRoom(
    @PrimaryKey(autoGenerate = true)
    var id : Int?=null,
    @ColumnInfo(name = "_id") var _id: String? = null,
    @ColumnInfo(name = "creator") var creator: LiveCreator? = null,
    @ColumnInfo(name = "title") var title: String? = null,
    @ColumnInfo(name = "schedule") var schedule: Long? = null,
    @ColumnInfo(name = "private") var `private`: Boolean? = null,
    @ColumnInfo(name = "live") var live: Boolean? = null,
    @ColumnInfo(name = "room_code") var room_code: String? = null,
    @ColumnInfo(name = "listeners") var listeners: List<Listener>? = emptyList(),
    @ColumnInfo(name = "sdpCandidate") var sdpCandidate: String = "",
    @ColumnInfo(name = "sdpMLineIndex") var sdpMLineIndex: String = "",
    @ColumnInfo(name = "sdpMid") var sdpMid: String = "",
    @ColumnInfo(name = "serverUrl") var serverUrl: String = "",
    @ColumnInfo(name = "start_time") var start_time: String? = null,
    @ColumnInfo(name = "type") var type: String = "",
    @ColumnInfo(name = "category") var category: List<String> = emptyList()
)

data class LiveCreator(
    val _id: String? = null,
    val channel_name: String? = null,
    val name: String? = null,
    val profile_picture: String? = null
)