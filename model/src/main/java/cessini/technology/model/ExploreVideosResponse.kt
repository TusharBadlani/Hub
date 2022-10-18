package cessini.technology.model

import com.google.gson.annotations.SerializedName


data class ExploreVideosResponse (
    val message: Message,
    val status: Boolean
)

data class Message (
    @SerializedName("Trending Technology")
    val trendingTechnology: List<ExploreVideoCategories>,

    @SerializedName("Trending News")
    val trendingNews: List<ExploreVideoCategories>,

    @SerializedName( "Entertainment")
    val entertainment: List<ExploreVideoCategories>,

    @SerializedName( "Health & Fitness")
    val healthFitness: List<ExploreVideoCategories>,

    @SerializedName( "Knowledge & Careers")
    val knowledgeCareers: List<ExploreVideoCategories>
)

data class ExploreVideoCategories (
    @SerializedName( "_id")
    val id: String,

    val profile: ExploreVideoProfile,
    val title: String,
    val description: String,
    val thumbnail: String,
    val duration: String,
    val category: String,

    @SerializedName( "upload_file")
    val uploadFile: String,

    val timestamp: Double
)

data class ExploreVideoProfile (
    @SerializedName( "_id")
    val id: String,

    val name: String,

    @SerializedName("channel_name")
    val channelName: String,

    @SerializedName( "profile_picture")
    val profilePicture: String
)
