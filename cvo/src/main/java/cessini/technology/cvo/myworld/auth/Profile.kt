package cessini.technology.cvo.myworld.auth

data class Profile(
    val name: String,
    val email: String,
    val location: String,
    val channelName: String,
    val profilePicture: String,
    val follower: Int,
    val following: Int,
)
