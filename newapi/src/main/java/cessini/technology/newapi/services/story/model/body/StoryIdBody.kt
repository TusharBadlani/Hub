package cessini.technology.newapi.services.story.model.body

import com.google.gson.annotations.SerializedName

data class StoryIdBody(
    @SerializedName(value = "story_id") val id: String,
)
