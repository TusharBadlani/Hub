package cessini.technology.cvo.profileModels

data class PublicProfileResponse(
    var id: String,
    var name: String?,
    var bio: String?,
    var channel_name: String?,
    var profile_picture: String?,
    var provider: String?,
    var following_count: Int,
    var follower_count: Int,
    var following: Boolean?
)