package cessini.technology.model

data class Song(
    val id: String,
    val title: String,
    val thumbnail: String,
    val duration: Int,
    val category: String,
    val url: String,
)
