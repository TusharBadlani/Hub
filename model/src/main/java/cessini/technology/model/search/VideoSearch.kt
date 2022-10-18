package cessini.technology.model.search

data class VideoSearch(
    val id: String,
    val title: String,
    val description: String,
    val category: List<String>,
    val thumbnail: String,
    val creatorName: String,
    val channelName: String,
)
