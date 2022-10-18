package cessini.technology.model.search

import cessini.technology.model.Creator
import cessini.technology.model.Listener

data class MySpaceSearch(
    val type: String,
    var keyword: String
)
data class RoomInfo(
    val id: String,
    val creator:Creator,
    val title: String,
    val schedule: Long,
    val private: Boolean,
    val live: Boolean,
    val code:String,
    val listeners: List<Listener>,
    val categories: List<Int>
)

data class Listener(
    val _id: String,
    val name: String,
    val profile_picture: String,
    val channel_name: String,

    // HandsOn Property
    var isHandsOn:Boolean = false
)