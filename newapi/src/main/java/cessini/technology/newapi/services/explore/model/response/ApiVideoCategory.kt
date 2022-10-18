package cessini.technology.newapi.services.explore.model.response

import com.google.gson.annotations.SerializedName

data class ApiVideoCategory(
    val data: Set<ApiVideoCategoryData>,
)

data class ApiVideoCategoryData(
    @SerializedName(value = "_id") val id: String,
    @SerializedName(value = "category") val name: String,
)
