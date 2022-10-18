package cessini.technology.profile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.model.User
import cessini.technology.profile.R
import cessini.technology.profile.controller.FollowerFollowingController
import cessini.technology.profile.databinding.FragmentFollowerFollowingTabBinding
import cessini.technology.profile.viewmodel.FollowerFollowingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowerTabFragment : Fragment() {
    private var _binding: FragmentFollowerFollowingTabBinding? = null
    val binding get() = _binding!!

    private val viewModel: FollowerFollowingViewModel by activityViewModels()
    private val baseViewModel by activityViewModels<BaseViewModel>()

    var controller: FollowerFollowingController? = null
    var ffListFollower = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.activity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_follower_following_tab, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.followerList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                ffListFollower = ArrayList(it)

                setFollowerFollowingRecycler()
            }
        }

        return binding.root
    }

    private fun setFollowerFollowingRecycler() {

        binding.recyclerFollowerFollowing.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controller = FollowerFollowingController(
                this@FollowerTabFragment,
                baseViewModel.id.value.orEmpty()
            )
            binding.recyclerFollowerFollowing.setController(controller!!)

            controller!!.followerFollowingList = ffListFollower

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.followerList.value = emptyList()

        controller = null
        _binding = null
    }
}
