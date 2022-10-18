package cessini.technology.model

data class VideoLikeCommentAndView(
    val id: String,
    val likes: Int,
    val comments: List<Comment>,
    val viewAndDuration: ViewAndDuration,
)
