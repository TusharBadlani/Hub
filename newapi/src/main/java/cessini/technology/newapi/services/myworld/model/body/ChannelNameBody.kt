package cessini.technology.newapi.services.myworld.model.body

import com.google.gson.annotations.SerializedName

data class ChannelNameBody(
    @SerializedName(value = "channel_name") val channelName: String,
)
