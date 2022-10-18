package cessini.technology.newapi.services.explore.model.response

import com.google.gson.annotations.SerializedName

data class ApiSongs(
    val message: Map<String, List<ApiSong>>,
)

data class ApiSong(
    @SerializedName(value = "_id") val id: String,
    val title: String,
    val thumbnail: String,
    val duration: Double,
    val category: String,
    @SerializedName(value = "upload_file") val url: String,
)
