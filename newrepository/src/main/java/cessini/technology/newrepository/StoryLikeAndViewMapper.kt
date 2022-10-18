package cessini.technology.newrepository

import cessini.technology.model.StoryLikeAndView
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.websocket.video.model.response.ApiStoryLikeAndView

fun ApiStoryLikeAndView.toModel() = StoryLikeAndView(
    id = id,
    likes = likes,
    viewAndDuration = viewAndDuration.toModel(),
)
