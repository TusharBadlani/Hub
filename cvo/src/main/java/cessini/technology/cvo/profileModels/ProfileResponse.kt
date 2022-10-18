package cessini.technology.cvo.profileModels

data class ProfileResponse(
    val id: String,
    var email: String,
    var displayName: String,
    var channelName: String?,
    var photoUrl: String?,
    var bio: String?
)