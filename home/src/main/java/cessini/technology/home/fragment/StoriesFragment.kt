package cessini.technology.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import cessini.technology.home.R
import cessini.technology.home.controller.StorySuggestionController
import cessini.technology.home.databinding.FragmentStoriesBinding
import cessini.technology.home.viewmodel.HomeFeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoriesFragment : Fragment() {

    companion object {
        private const val TAG = "StoriesFragment"
    }

    val viewModel: HomeFeedViewModel by activityViewModels()

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!

    var storySuggestionController: StorySuggestionController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_stories, container, false
        )

        /**Setting up thestory_view viewModel to the Binding.*/

        binding.homeFeedViewModel = viewModel//attach your viewModel to xml
        /**Setting a lifecycleOwner as this Fragment.*/
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d("CheckNav", "StoriesFragment, ${activity?.localClassName}")

        Log.d("StorySugAdapter", "Observing Next")
        viewModel.storiesFetchList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                Log.d(TAG, "Data updated")

                /**Setting up the viewers Suggestion View.*/
                //(TODO) Use DI if possible
                val controller = StorySuggestionController(viewModel, this)
                Log.d(TAG, "HomeFeedStories : $it")
                controller.allStories = it
                binding.setStorySuggestionController = controller
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        storySuggestionController = null

        _binding = null
    }

}