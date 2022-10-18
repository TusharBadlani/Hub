package cessini.technology.newapi.services.myworld.model.response

data class ApiRoomMessages(
    val id: String,
    val userName: String,
    val userProfileImage: String,
    val message: String
)

data class ApiCreateRoomMessage(
    val data: List<ApiRoomMessages>
)
