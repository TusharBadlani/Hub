package cessini.technology.home.model

data class JoinRoomSocketEventPayload(
    val email: String,
    val room: String,
    val user: User
)