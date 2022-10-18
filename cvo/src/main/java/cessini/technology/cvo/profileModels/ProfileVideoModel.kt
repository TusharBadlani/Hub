package cessini.technology.cvo.profileModels

import cessini.technology.cvo.exploremodels.ProfileModel


data class ProfileVideoModel(
    var id: String?,
    var category: String?,
    var title: String?,
    var description: String?,
    var thumbnail: String?,
    var duration: Int?,
    var upload_file: String?,
    var timestamp: String?,
    var views: Int?,
    var likes: Int?,
    var comments: Int?,
    var profile: ProfileModel? = null
)
