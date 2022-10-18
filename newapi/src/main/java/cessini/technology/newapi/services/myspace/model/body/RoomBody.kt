package cessini.technology.newapi.services.myspace.model.body

data class RoomBody(
    val title: String,
    val schedule: Long,
    val private: Boolean,
    val users: List<String>,
    val category: List<String>
)
