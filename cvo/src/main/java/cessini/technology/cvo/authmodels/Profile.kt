package cessini.technology.cvo.authmodels

data class Profile(
    var id: String,
    var email: String,
    var name: String?,
    var bio: String?,
    var channel_name: String?,
    var profile_picture: String?,
    var location: String?,
    var provider: String?,
    var following_count: Int,
    var follower_count: Int,
)