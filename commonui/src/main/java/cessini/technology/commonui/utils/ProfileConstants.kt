package cessini.technology.commonui.utils

import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.cvo.exploremodels.SearchVideoModel
import cessini.technology.cvo.profileModels.StoryProfileModel

object ProfileConstants {
    var creatorModel: SearchCreatorApiResponse? = null

    var videoModel: SearchVideoModel? = null
    var public: Boolean = false

    var storyProfileModel: StoryProfileModel? = null
    var story: Boolean = false
}