package cessini.technology.notifications

data class MessagesList(
    val with:String,
    val username:String,
    val profile_picture:String,
    val message:String?,
    val time: Long,
    val read: Boolean,
    val sender: Boolean
)
