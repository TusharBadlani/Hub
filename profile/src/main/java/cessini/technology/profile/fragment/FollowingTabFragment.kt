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
import cessini.technology.profile.controller.FollowingController
import cessini.technology.profile.databinding.FragmentFollowingTabBinding
import cessini.technology.profile.viewmodel.FollowerFollowingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowingTabFragment : Fragment() {
    private var _binding: FragmentFollowingTabBinding? = null
    val binding get() = _binding!!

    private val viewModel: FollowerFollowingViewModel by activityViewModels()
    private val baseViewModel by activityViewModels<BaseViewModel>()

    var controller: FollowingController? = null
    var ffListFollowing = ArrayList<User>()

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
            R.layout.fragment_following_tab, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.followingList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                ffListFollowing = ArrayList(it)

                setFollowerFollowingRecycler()
            }
        }

        return binding.root
    }

    private fun setFollowerFollowingRecycler() {
        binding.recyclerFollowing.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controller = FollowingController(
                this@FollowingTabFragment,
                baseViewModel.id.value.orEmpty()
            )
            binding.recyclerFollowing.setController(controller!!)

            controller!!.followingList = ffListFollowing

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.followingList.value = emptyList()

        controller = null
        _binding = null
    }
}
