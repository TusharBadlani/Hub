package cessini.technology.cvo.exploremodels

import cessini.technology.cvo.profileModels.ProfileVideoModel


data class CategoryItem(
    var id: String?,
    var category: String?,
    var videos: ArrayList<ProfileVideoModel>?
)
