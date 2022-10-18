package cessini.technology.model

data class TopProfile(
    val id: String,
    val name: String,
    val channelName: String,
    val profilePicture: String,
    val is_following: Boolean?
)
