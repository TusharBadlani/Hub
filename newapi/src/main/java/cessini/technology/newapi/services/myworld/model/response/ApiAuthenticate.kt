package cessini.technology.newapi.services.myworld.model.response

import com.google.gson.annotations.SerializedName

data class ApiAuthenticate(
    val data: ApiProfileData,
)

data class ApiProfileData(
    val profile: ApiProfile,
    val token: Token,
)

data class ApiGetProfile(
    val data: ApiProfile,
)

data class ApiProfile(
    val id: String,
    val name: String,
    val email: String,
    val bio: String,
    val provider: String,
    val location: String,
    @SerializedName(value = "profile_picture") val profilePicture: String,
    @SerializedName(value = "verified") val verified: Boolean,
    @SerializedName(value = "channel_name") val channelName: String,
    @SerializedName(value = "area_of_expert") val expertise: String,
    @SerializedName(value = "follower_count") val followerCount: Int,
    @SerializedName(value = "following_count") val followingCount: Int,
    @SerializedName(value = "is_following") val following: Boolean = false,
)

data class Token(
    @SerializedName(value = "access_token") val access: String,
    @SerializedName(value = "refresh_token") val refresh: String,
)
