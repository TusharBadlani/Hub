package cessini.technology.cvo.profileModels

data class ProfileMediaModel(
    var id: String?,
    var stories: ArrayList<ProfileStoryModel>?,
    var videos: ArrayList<ProfileVideoModel>?
)
