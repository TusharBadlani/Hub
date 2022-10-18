package cessini.technology.explore.controller

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.explore.userSearchCreatorItem
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import com.airbnb.epoxy.AsyncEpoxyController

class UserSearchCreatorController(var context: Context, var activity: FragmentActivity?) :
    AsyncEpoxyController() {

    var allCreators = emptyList<SearchCreatorApiResponse>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        allCreators.forEachIndexed { index, searchCreatorModel ->
            Log.i("UserSearchCreator curr:", allCreators[index].id!!.toString())
            Log.i(
                "UserSearchCreator orig:",
                (activity as HomeActivity).baseViewModel.id.value.toString()
            )
            if (allCreators[index].id!!.toString() != (activity as HomeActivity).baseViewModel.id.value) {
                userSearchCreatorItem {
                    id(index)
                    creator(searchCreatorModel)
                    onClickCreator { _, _, _, _ ->
                        ProfileConstants.creatorModel = searchCreatorModel
                        (activity as ToFlowNavigable).navigateToFlow(
                            NavigationFlow.GlobalProfileFlow
                        )
                    }
                }
            }
        }
    }
}
