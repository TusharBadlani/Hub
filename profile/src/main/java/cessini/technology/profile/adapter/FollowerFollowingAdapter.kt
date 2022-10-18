package cessini.technology.profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cessini.technology.profile.fragment.FollowerTabFragment
import cessini.technology.profile.fragment.FollowingTabFragment

class FollowerFollowingAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowerTabFragment()
            }
            1 -> {
                FollowingTabFragment()
            }
            else -> {
                FollowerTabFragment()
            }
        }
    }

}