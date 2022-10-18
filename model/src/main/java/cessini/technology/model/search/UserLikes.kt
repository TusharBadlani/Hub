package cessini.technology.model.search

import com.google.gson.annotations.SerializedName


data class UserLikes(
    @SerializedName("message" ) var message : ArrayList<MessageL> = arrayListOf(),
    @SerializedName("status"  ) var status  : Boolean?           = null
)
 data class MessageL(
     @SerializedName("_id"       ) var Id       : String? = null,
     @SerializedName("user_id"   ) var userId   : String? = null,
     @SerializedName("room_code" ) var roomCode : String? = null
 )