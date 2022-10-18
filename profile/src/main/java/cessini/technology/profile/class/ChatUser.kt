package cessini.technology.profile.`class`

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class ChatUser(
    @SerializedName("userid") val userid: String,
    @SerializedName("username") val username: String,
    @SerializedName("channel_name") val channel_name: String
) {

    fun getJSONuser(): JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }
}

class ChatUserOther(
    @SerializedName("username") val username: String
) {

    fun getJSONuser(): JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }
}
