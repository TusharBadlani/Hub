package cessini.technology.model

data class CreateRoomRequest(
    val userId: String,
    val userName: String,
    val profileImage: String,
    val message: String
)
