package cessini.technology.model

data class Profile(
    val id: String,
    val name: String,
    val email: String,
    val expertise:String,
    val channelName: String,
    val provider: String,
    val bio: String,
    val location: String,
    val profilePicture: String,
    val followerCount: Int,
    val followingCount: Int,
    val following: Boolean,
    val verified:Boolean
)
