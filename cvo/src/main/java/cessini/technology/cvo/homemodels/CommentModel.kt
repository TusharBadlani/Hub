package cessini.technology.cvo.homemodels

import cessini.technology.cvo.exploremodels.ProfileModel


data class CommentModel(
    var id: Int,
    var profile: ProfileModel,
    var comment: String,
    var timestamp: String
)