package cessini.technology.profile.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cessini.technology.profile.fragment.UserProfileMore
import cessini.technology.profile.fragment.UserProfileRoom
import cessini.technology.profile.fragment.UserProfileVideo

class ProfileTabViewAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UserProfileRoom()
            }
            1 -> {
                UserProfileVideo()
            }
            2 -> {
                UserProfileMore()
            }
            else -> {
                UserProfileRoom()
            }
        }
    }
}