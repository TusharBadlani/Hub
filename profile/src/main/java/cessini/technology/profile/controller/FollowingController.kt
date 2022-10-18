package cessini.technology.profile.controller

import android.util.Log
import cessini.technology.commonui.common.navigateToProfile
import cessini.technology.model.User
import cessini.technology.profile.followerFollowingItem
import cessini.technology.profile.fragment.FollowingTabFragment
import com.airbnb.epoxy.AsyncEpoxyController

class FollowingController(
    private val followingTabFragment: FollowingTabFragment,
    private val idFromBaseViewModel: String
) : AsyncEpoxyController() {

    var followingList = ArrayList<User>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        Log.d("FFController", followingList.toString())
        followingList.forEachIndexed { index, followingProfile ->
            followerFollowingItem {
                id(index)
                user(followingProfile)
                onClick { _ ->
                    followingTabFragment.navigateToProfile(
                        followingProfile.id,
                        idFromBaseViewModel
                    )
                }
            }
        }
    }
}
