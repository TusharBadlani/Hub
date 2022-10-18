package cessini.technology.newapi.model;

data class RoomTimeline(
    var message:List<Message>,
    var status: Boolean?
);
data class LiveRoomData(
    var ldata:List<LiveRoomData>,
)
data class LiveRoomTimeline(
    var _id: String,
    var room_id:String,
    var room_created:String,
    var room_count:Int,
    var client_connected:List<String>
)

data class Message(
    var _id: String,
    var title: String,
    var schedule: String,
    var private: Boolean,
    var live: Boolean,
    var room_code: String,
    var sdpCandidate: String,
    var sdpMLineIndex: String,
    var sdpMid: String,
    var serverUrl: String,
    var start_time: String,
    var type: String,
    var listeners: List<Listeners>,
    var creator: Creator
)


data class Creator(
    val _id: String? = null,
    val channel_name: String? = null,
    val name: String? = null,
    val profile_picture: String? = null
)


data class Listeners(
    val _id: String? = null,
    val channel_name: String? = null,
    val name: String? = null,
    val profile_picture: String? = null
)

open class CreatorListeners(
    var isCreator:Boolean,
    val _id: String? = null,
    val channel_name: String? = null,
    val name: String? = null,
    val profile_picture: String? = null
)