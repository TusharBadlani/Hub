package cessini.technology.profile.`class`

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

class getMessage(@SerializedName("userid") val userid:String, @SerializedName("with_userid") val with_userid:String) {
    fun getMessages() : JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }
}