package cessini.technology.cvo.exploremodels

import cessini.technology.model.Creator
import cessini.technology.model.Listener

data class SearchMySpaceModel(

    val id: String?,
    val creator: Creator?,
    val title: String?,
    val schedule: Long?,
    val private: Boolean?,
    val live: Boolean?,
    val room_code:String?,
    val listeners: List<Listener>? ,
    val category: List<String>?
)
