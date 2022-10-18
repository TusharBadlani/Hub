package cessini.technology.home.model

data class Message(
    val created_at: String,
    val message: String,
    val name: String,
    val profile_picture: String,
    val user_id: String
)