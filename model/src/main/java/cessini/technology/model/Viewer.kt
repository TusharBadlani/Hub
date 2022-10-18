package cessini.technology.model

data class Viewer(
    val id: String,
    val profileId: String,
    val caption: String,
    val thumbnail: String,
    val duration: Int,
    val url: String,
    val timestamp: Float,
)
