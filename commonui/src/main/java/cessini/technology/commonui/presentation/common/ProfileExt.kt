package cessini.technology.commonui.presentation.common

import androidx.fragment.app.Fragment
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.navigation.ToFlowNavigable

fun Fragment.navigateToProfile(id: String, idFromBaseViewModel: String) {
    if (id == idFromBaseViewModel) {
        (requireActivity() as ToFlowNavigable).navigateToFlow(
            cessini.technology.navigation.NavigationFlow.ProfileFlow
        )
        return
    }
    ProfileConstants.creatorModel = SearchCreatorApiResponse(id = id)

    (requireActivity() as ToFlowNavigable).navigateToFlow(
        cessini.technology.navigation.NavigationFlow.GlobalProfileFlow
    )
}
