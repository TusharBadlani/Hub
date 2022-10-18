package cessini.technology.model

import com.google.gson.annotations.SerializedName

data class suggestionroomresponse(

    @SerializedName("message" ) val message : SuggestionRoomMessage,
    @SerializedName("status"  ) val status  : Boolean
)
data class SuggestionRoomMessage(
    @SerializedName("Trending Technology")
    val TrendingTechnology :ArrayList<RoomResponse> = arrayListOf(),
    @SerializedName("Trending News")
    var TrendingNews :ArrayList<RoomResponse> =arrayListOf(),
    @SerializedName("Health & Fitness")
    var HealthFitness :ArrayList<RoomResponse> = arrayListOf(),
    @SerializedName("Knowledge & Careers")
    var Knowledgecarrers :ArrayList<RoomResponse> = arrayListOf()

)
data class RoomResponse(

    @SerializedName("_id"           ) var Id            : String?              = null,
    @SerializedName("creator"       ) var creator       : Creators,
    @SerializedName("title"         ) var title         : String,
    @SerializedName("schedule"      ) var schedule      : String?              = null,
    @SerializedName("private"       ) var private       : Boolean?             = null,
    @SerializedName("live"          ) var live          : Boolean?             = null,
    @SerializedName("room_code"     ) var roomCode      : String?              = null,
    @SerializedName("listeners"     ) var listeners     : ArrayList<Listeners> ,
    @SerializedName("sdpCandidate"  ) var sdpCandidate  : String?              = null,
    @SerializedName("sdpMLineIndex" ) var sdpMLineIndex : String?              = null,
    @SerializedName("sdpMid"        ) var sdpMid        : String?              = null,
    @SerializedName("serverUrl"     ) var serverUrl     : String?              = null,
    @SerializedName("start_time"    ) var startTime     : String?              = null,
    @SerializedName("category"      ) var category      : ArrayList<String>?= null

)
data class Creators(

    @SerializedName("_id") val id: String,
    @SerializedName("profile_picture") val profilePicture : String,
    @SerializedName("channel_name") val channelName: String,
    @SerializedName("name") val name: String

)
data class Listeners(

    @SerializedName("_id"             ) var Id             : String,
    @SerializedName("profile_picture" ) var profilePicture : String,
    @SerializedName("channel_name"    ) var channelName    : String,
    @SerializedName("name"            ) var name           : String

)
