package cessini.technology.home.webSockets.model

import com.google.gson.annotations.SerializedName

data class HomeFeedSocketResponse(
    @SerializedName("data") val `data`: List<DataResponse>,
    @SerializedName("message") val message: String,
    @SerializedName("meta") val meta: MetaResponse,
    @SerializedName("status") val status: Boolean
)