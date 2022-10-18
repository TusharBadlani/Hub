package cessini.technology.newapi.services.myworld.model.response

import com.google.gson.annotations.SerializedName

data class OnBoardingSubmissionResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("deviceId")
    val deviceId: String?,
    @SerializedName("status")
    val status: Boolean?,
)