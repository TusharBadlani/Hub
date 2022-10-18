package cessini.technology.model

data class Notification(
    val type: Type,
    val id: String,
    val message: String,
    val image: String,
    val time: Long,
    val seen: Boolean,
) {
    enum class Type {
        ERROR,
        VIDEO_LIKE,
        STORY_LIKE,
        VIDEO_COMMENT,
        STORY_COMMENT,
        FOLLOW,
        ROOM_JOIN_REQUEST,
        ROOM_JOIN_REQUEST_ACCEPTED,
        VIDEO_UPLOAD,
        CREATE_ROOM_REQUEST,
    }
}
