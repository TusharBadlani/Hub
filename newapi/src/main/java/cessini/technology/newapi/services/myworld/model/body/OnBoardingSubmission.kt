package cessini.technology.newapi.services.myworld.model.body

import com.google.gson.annotations.SerializedName

data class OnBoardingSubmission(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("sub_category")
    val subCategories: List<String>,
)
