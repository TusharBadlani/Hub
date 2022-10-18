package cessini.technology.newapi.services.explore.model.body

import com.google.gson.annotations.SerializedName

data class UserRegistrationBody(
    @SerializedName(value = "device_id") val uuid: String,
    val categories: Set<String>,
    val sub_categories: Set<String>

)
