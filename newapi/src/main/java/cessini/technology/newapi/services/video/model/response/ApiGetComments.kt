package cessini.technology.newapi.services.video.model.response

import cessini.technology.newapi.model.ApiComment

data class ApiGetComments(
    val data: ApiCommentData = ApiCommentData(),
)

data class ApiCommentData(
    val comments: List<ApiComment> = emptyList(),
)
