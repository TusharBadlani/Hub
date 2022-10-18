package cessini.technology.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.model.RecordedVideo
import cessini.technology.model.Room
import cessini.technology.model.recordmyspacegrid.recordGrid
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.profile.R
import cessini.technology.profile.controller.SaveVideoController
import cessini.technology.profile.databinding.FragmentUserProfileMoreBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileMore : Fragment() {
    companion object {
        private const val TAG = "ProfileSaveTabFragment"
    }

    private var _binding: FragmentUserProfileMoreBinding?=null
    val binding get()= _binding!!

    @Inject
    lateinit var roomRepository: RoomRepository

    var controller: SaveVideoController?=null
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**Setting Up the View Model as Profile View Model.*/
        viewModel = activity?.run {
            ViewModelProvider(this)[ProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View  {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_profile_more,
            container,
            false
        )

        setUpController()

        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated: At least It's getting created")

        viewModel.allRecordedVideo.observe(viewLifecycleOwner) {
            Log.e(TAG, "${viewModel.allRecordedVideo.value}")
            if (!it.isNullOrEmpty()) {
                hideShimmer()
                Log.e(TAG, "${it}")
                var recordItems = getItems(it)
                controller!!.recordGrids = recordItems

            } else {
                Log.e(TAG, "No Save Video Found. None Are available")
                noData()

            }
            hideShimmer()
        }
    }

    private fun getItems(recordedVideo: RecordedVideo): MutableList<recordGrid> {
        val result:MutableList<recordGrid> = mutableListOf()
        recordedVideo.forEach { item ->
            result.add(recordGrid(item.thumbnail,item.title,item.username,item.category,""))
        }
        return  result
    }

    override fun onResume() {

        super.onResume()
    }

    private fun setUpController(){
        binding.saveProfileFragmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            controller = SaveVideoController(requireContext())
            setController(controller!!)
        }
    }

    private fun hideShimmer() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            binding.profileSaveShimmer.visibility = View.GONE
            binding.saveProfileFragmentRecyclerView.visibility = View.VISIBLE
            binding.profileSaveText.visibility = View.GONE
            binding.noSaveLabel.visibility = View.GONE

        }
    }

    private fun noData() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000)
            binding.profileSaveShimmer.visibility = View.GONE
            binding.noSaveLabel.visibility = View.VISIBLE
            binding.profileSaveText.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        controller = null
        _binding = null
    }
}