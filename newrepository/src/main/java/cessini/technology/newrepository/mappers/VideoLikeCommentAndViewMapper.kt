package cessini.technology.newrepository.mappers

import cessini.technology.model.VideoLikeCommentAndView
import cessini.technology.newrepository.websocket.video.model.response.ApiVideoLikeCommentAndView

fun ApiVideoLikeCommentAndView.toModel() = VideoLikeCommentAndView(
    id = id,
    likes = likes,
    comments = comments.comments.map { it.toModel() },
    viewAndDuration = views.toModel(),
)
