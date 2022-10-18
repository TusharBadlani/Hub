package cessini.technology.notifications.epoxyChat

data class ResponseJson (
    val _id:Any,
    val with:String,
    val username:String,
    val profile_picture:String,
    val message:String,
    val time: Long,
    val read: Boolean,
    val sender: Boolean
)