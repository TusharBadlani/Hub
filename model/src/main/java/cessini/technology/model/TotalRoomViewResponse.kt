package cessini.technology.model

data class TotalRoomViewResponse(
    val `data`: RoomViewData,
    val message: String,
    val status: Boolean
)

data class RoomViewData(
    val _id: String,
    val id: String,
    val total_duration: Int,
    val total_viewers: Int
)