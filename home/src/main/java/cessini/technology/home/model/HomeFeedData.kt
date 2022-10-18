package cessini.technology.home.model

import android.content.Context

data class HomeFeedData(
    val __v: Int,
    val _id: String,
    val admin: String,
    val adminSocket: String,
    val allowedUsers: List<AllowedUser>,
    val chat: String,
    val createdAt: String,
    val description: String,
    val live: Boolean,
    val messages: List<Message>,
    val otherUsers: List<Any>,
    val room: String,
    val sub_category: List<String>,
    val title: String
)