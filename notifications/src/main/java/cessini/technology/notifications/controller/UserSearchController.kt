package cessini.technology.notifications.controller

import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.notifications.userSearchItem
import com.airbnb.epoxy.AsyncEpoxyController

class UserSearchController(private val callbacks: AdapterCallbacks) : AsyncEpoxyController() {

    var allCreatorsExceptCurrentUser = emptyList<SearchCreatorApiResponse>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        allCreatorsExceptCurrentUser.forEachIndexed { index, searchCreatorModel ->
            userSearchItem {
                id(index)
                creator(searchCreatorModel)
                onClickCreator { _, _, _, _ ->
                    ProfileConstants.creatorModel = searchCreatorModel
                    this@UserSearchController.callbacks.onUserSearchItemClicked(
                        searchCreatorModel.id.orEmpty(),
                        searchCreatorModel.channel_name
                    )
                }
            }
        }
    }

    interface AdapterCallbacks {
        fun onUserSearchItemClicked(id: String, channelName: String)
    }

}