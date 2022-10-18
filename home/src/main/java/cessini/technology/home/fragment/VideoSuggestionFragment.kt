package cessini.technology.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cessini.technology.home.R
import cessini.technology.home.controller.VideoSuggestionController
import cessini.technology.home.databinding.FragmentVideoSuggestionBinding
import cessini.technology.home.viewmodel.HomeFeedViewModel
import cessini.technology.home.viewmodel.SocketFeedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class VideoSuggestionFragment : Fragment() {

    val viewModel: HomeFeedViewModel by activityViewModels()
    val socketViewModel: SocketFeedViewModel by activityViewModels()

    private var _binding: FragmentVideoSuggestionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_video_suggestion, container, false
        )

        /**Setting up the viewModel to the Binding.*/
//        viewModel = ViewModelProvider(this).get(HomeFeedViewModel::class.java)
        binding.videoSuggestionViewModel = viewModel//attach your viewModel to xml
        /**Setting a lifecycleOwner as this Fragment.*/
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d("VidSugAdapter", "Fragment Called")

        socketViewModel.suggestedVideos.filter { it.isNotEmpty() }.onEach {
            binding.setVideoSuggestionController =
                VideoSuggestionController(socketViewModel).apply { allVideos = ArrayList(it) }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}