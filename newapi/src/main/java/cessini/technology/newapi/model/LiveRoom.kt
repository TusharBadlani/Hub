package cessini.technology.newapi.model


data class LiveRoom(
    val _id: String? = null,
    val creator: Creator? = null,
    val listeners: List<Any>? = null,
    val live: Boolean? = null,
    val `private`: Boolean? = null,
    val room_code: String? = null,
    val schedule: Long? = null,
    val start_time: String? = null,
    val title: String? = null
)