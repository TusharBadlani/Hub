package cessini.technology.model

import com.google.gson.annotations.SerializedName

data class TrendingRooms(
    @SerializedName("message") var message : List<MessageT>, //List<List<MessageT>>
    @SerializedName("status") var status : Boolean
)

data class MessageT (

    /*@SerializedName("_id") var Id : String,

    @SerializedName("title") var title : String,
    @SerializedName("schedule") var schedule : String,
    @SerializedName("private") var private : Boolean,
    @SerializedName("live") var live : Boolean,
    @SerializedName("room_code") var roomCode : String,
    @SerializedName("listeners") var listeners : List<ListenersT>,
    @SerializedName("sdpCandidate") var sdpCandidate : String,
    @SerializedName("sdpMLineIndex") var sdpMLineIndex : String,
    @SerializedName("sdpMid") var sdpMid : String,
    @SerializedName("serverUrl") var serverUrl : String,
    @SerializedName("start_time") var startTime : String,
    @SerializedName("type") var type : String,
    @SerializedName("category") var category : List<String>*/
    @SerializedName("title") var title:String,
    @SerializedName("description") var description:String,
    @SerializedName("room") var room:String,
    @SerializedName("admin") var admin:String,
    @SerializedName("live") var live:Boolean,
    @SerializedName("liveMembers") var liveMembers:Int,
    @SerializedName("creator") var creator : CreatorT,
    @SerializedName("allowedUsers") var allowedUser : List<AllowedUserT>
)

data class CreatorT (

    @SerializedName("id") var Id : String,
    @SerializedName("profilePicture") var profilePicture : String,
    @SerializedName("channelName") var channelName : String,
    @SerializedName("name") var name : String,
    @SerializedName("socker") var socket:String,
    @SerializedName("email") var email:String

)

data class AllowedUserT (

    @SerializedName("id") var Id : String,
    @SerializedName("profilePicture") var profilePicture : String,
    @SerializedName("channelName") var channelName : String,
    @SerializedName("name") var name : String,
    @SerializedName("socker") var socket:String,
    @SerializedName("email") var email:String

)