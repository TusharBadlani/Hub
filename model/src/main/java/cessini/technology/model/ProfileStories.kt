package cessini.technology.model

data class ProfileStories(
    val profileId: String,
    val picture: String,
    val viewers: List<Viewer>,
)
