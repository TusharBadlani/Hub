package cessini.technology.profile.controller

import android.util.Log
import cessini.technology.commonui.common.navigateToProfile
import cessini.technology.model.User
import cessini.technology.profile.followerFollowingItem
import cessini.technology.profile.fragment.FollowerTabFragment
import com.airbnb.epoxy.AsyncEpoxyController

class FollowerFollowingController(
    private val epoxyRecyclerView: FollowerTabFragment,
    private val idFromBaseViewModel: String
) : AsyncEpoxyController() {

    var followerFollowingList = ArrayList<User>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        Log.d("FFController", followerFollowingList.toString())
        followerFollowingList.forEachIndexed { index, followingProfile ->
            followerFollowingItem {
                id(index)
                user(followingProfile)
                onClick { _ ->
                    epoxyRecyclerView.navigateToProfile(
                        followingProfile.id,
                        idFromBaseViewModel
                    )
                }
            }

        }
    }
}
