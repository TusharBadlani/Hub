package cessini.technology.model

import com.google.gson.annotations.SerializedName

data class Subcategory(
    @SerializedName( "_id")
    val id: String,

    @SerializedName( "category_id")
    val categoryID: String,

    @SerializedName( "sub_category")
    val subCategory: String
)
