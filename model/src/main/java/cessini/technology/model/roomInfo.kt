package cessini.technology.model

data class roomInfo(
    val title:String,
    val roomCode: String?,
    val datas :MutableList<RoomUsers>
)

data class RoomUsers(
    val id:String,
    val title: String,
    val name:String,
val url :String,
val channelName:String
)
