package cessini.technology.newapi.services.myworld.model.body

import com.google.gson.annotations.SerializedName

data class AuthBody(
    @SerializedName(value = "auth_token") val googleIdToken: String,
)
