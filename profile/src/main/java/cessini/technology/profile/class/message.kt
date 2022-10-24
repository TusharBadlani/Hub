package cessini.technology.profile.`class`

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.json.JSONObject


class message(@SerializedName("sender_id") val sender_id:String,
              @SerializedName("receiver_id")  val receiver_id:String,
              @SerializedName("message")  val message:String) {

    fun getMessage() : JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }


}