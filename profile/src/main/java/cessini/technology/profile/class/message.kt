package cessini.technology.profile.`class`

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.model.Profile
import cessini.technology.newrepository.datastores.ProfileStore
import cessini.technology.newrepository.datastores.ProfileStoreKeys
import cessini.technology.newrepository.datastores.ProfileStoreKeys.id
import cessini.technology.newrepository.profileRepository.ProfileRepository
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject


class message(@SerializedName("sender_id") val sender_id:String,
              @SerializedName("receiver_id")  val receiver_id:String,
              @SerializedName("message")  val message:String) {

    fun getMessage() : JSONObject {
        val jsonString = Gson().toJson(this)  // json string
        return JSONObject(jsonString)
    }


}