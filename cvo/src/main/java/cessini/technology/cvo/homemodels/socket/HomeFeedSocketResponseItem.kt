package cessini.technology.cvo.homemodels.socket

import com.google.gson.annotations.SerializedName


data class HomeFeedSocketResponseItem(
    val category: Int?,
    val comments: Int?,
    val description: String?,
    val duration: Int?,
    val id: String,
    val likes: Int?,
    val profile: Profile?,
    val thumbnail: String?,
    val timestamp: String?,
    val title: String?,
    @SerializedName("upload_file") val uploadFile: String?,
    val views: Int?,
)
