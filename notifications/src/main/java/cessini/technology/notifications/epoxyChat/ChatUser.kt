package cessini.technology.notifications.epoxyChat


import com.google.gson.Gson
import org.json.JSONObject

class ChatUser(
    val userid: String,
    val username: String,
    val channel_name: String
) {
    fun getJSONuser(): JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }
}


class ChatId(val userid: String) {
    fun getJSONuser(): JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }
}