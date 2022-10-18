package cessini.technology.model

data class Video(
    val id: String,
    val profile: VideoProfile,
    val title: String,
    val description: String,
    val thumbnail: String,
    val duration: Int,
    val category: List<String>,
    val url: String,
    val timestamp: Float,
    val viewCount: Int = 0,
    val roomCount: Int = 0,
    /* Custom field for labeling
    *   0 for FIRST,
    *   1 for MIDDLE,
    *   2 for LAST
    *
    * */
    var positionType: Int = 1
)

data class VideoProfile(
    val id: String,
    val name: String,
    val channel: String,
    val picture: String,
)
