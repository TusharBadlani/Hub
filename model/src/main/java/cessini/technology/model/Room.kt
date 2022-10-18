package cessini.technology.model

data class Room(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val creator: Creator = Creator(),
    val time: Long = 0L,
    val private: Boolean = false,
    val live: Boolean = false,
    var listeners: List<Listener> = emptyList(),
    val categories: List<String> = emptyList()
)

data class Listener(
    val _id: String,
    val name: String,
    val profile_picture: String,
    val channel_name: String,

    // HandsOn Property
    var isHandsOn:Boolean = false
)
