package cessini.technology.model

data class RoomViews(
    val `data`: RoomViewsData,
    val message: String,
    val status: Boolean
)
data class RoomViewsData(
    val comments: Any,
    val room_code: String,
    val users_likes: List<Any>,
    val viewers: List<ViewerX>,
    val views: Views
)
data class ViewerX(
    val _id: String,
    val profile: ProfileX
)
data class Views(
    val _id: String,
    val id: String,
    val total_duration: Int,
    val total_viewers: Int
)
data class ProfileX(
    val channel_name: String,
    val profile_picture: String
)