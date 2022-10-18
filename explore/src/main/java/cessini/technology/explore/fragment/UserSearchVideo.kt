package cessini.technology.explore.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.explore.R
import cessini.technology.explore.controller.UserSearchHistoryController
import cessini.technology.explore.controller.UserSearchVideoController
import cessini.technology.explore.databinding.FragmentUserSearchVideoBinding
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.newrepository.video.VideoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserSearchVideo : Fragment() {

    @Inject
    lateinit var videoRepository: VideoRepository

    val viewModel: ExploreSearchViewModel by activityViewModels()

    private var _binding: FragmentUserSearchVideoBinding? = null
    val binding get() = _binding!!

    var controller: UserSearchVideoController? = null
    var controllerHistory: UserSearchHistoryController? = null
    lateinit var parentFrag: ExploreSearchFragment
    var history: ArrayList<SearchHistoryEntity> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_search_video, container, false)
        binding.videoViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.contextVideo = requireContext()

        setVideosRecycler()
        viewModel.videoResponseModels.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                controller!!.allVideos = it
                controller!!.videosFilterList = it
            }
        })


        parentFrag = this@UserSearchVideo.parentFragment as ExploreSearchFragment


        return binding.root
    }

    override fun onResume() {
        super.onResume()

        setHistory()
        binding.recyclerViewVideoHistory.visibility = View.VISIBLE
        viewModel.videoHistory.observe(viewLifecycleOwner, Observer {

            Log.i("Explore", "Outside: $it")
            if (it != null) {

                history = it
                Log.i("Explore", "Inside: $it")
                controllerHistory!!.historyList = it

                if (history.size == 0) {

                    binding.layoutVideo.visibility = View.VISIBLE
                    binding.recyclerViewVideoHistory.visibility = View.GONE

                } else {
                    binding.layoutVideo.visibility = View.GONE
//                    binding.recyclerViewVideoHistory.visibility = View.VISIBLE
                }
            }

        })

        val parentFrag: ExploreSearchFragment =
            this@UserSearchVideo.parentFragment as ExploreSearchFragment
        parentFrag.binding.searchViewHeader.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.recyclerViewVideoHistory.visibility = View.GONE

                Log.i("Submit searched texts: ", query!!)
                viewModel.fetchSearchVideosQueryAPI(query)
                viewModel.videoResponseModels.observe(viewLifecycleOwner, Observer {


                    if (it != null) {
                        if (it.size > 0) {
                            binding.layoutVideo.visibility = View.GONE
                            binding.recyclerViewVideos.visibility = View.VISIBLE

                        } else if (it.size == 0) {
                            binding.layoutVideo.visibility = View.GONE
                            binding.recyclerViewVideos.visibility = View.GONE
                        }
                    }

                    binding.recyclerViewVideoHistory.visibility = View.GONE
                })

                if (query == "") {
                    binding.layoutVideo.visibility = View.GONE
                    binding.recyclerViewVideos.visibility = View.GONE
                    binding.recyclerViewVideoHistory.visibility = View.VISIBLE
                }

                //Insert to db
                val entity =
                    SearchHistoryEntity(id = "$query Video", category = "Video", query = query)
                viewModel.insertSearchQueryToDB(entity)
                Log.d("Explore", entity.toString())

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.i("Video text pattern: ", newText!!)

                viewModel.videoResponseModels.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        Log.e("it",it.toString())
                        if (it.size > 0) {
                            binding.layoutVideo.visibility = View.GONE
                            binding.recyclerViewVideos.visibility = View.VISIBLE
                            binding.layoutNoResultVideo.visibility = View.GONE
                        } else if (it.size == 0) {
                            binding.layoutVideo.visibility = View.GONE
                            binding.recyclerViewVideos.visibility = View.GONE
                            binding.layoutNoResultVideo.visibility = View.VISIBLE
                        }
                    }

                })

                if (newText.isEmpty()) {
                    binding.recyclerViewVideoHistory.visibility = View.VISIBLE
                    binding.recyclerViewVideos.visibility = View.GONE
                    binding.layoutNoResultVideo.visibility = View.GONE
                }

                if (newText.length == 2 || newText.length > 2) {
                    viewModel.fetchSearchVideosQueryAPI(newText)
                    binding.recyclerViewVideoHistory.visibility = View.GONE
                    binding.recyclerViewVideos.visibility = View.VISIBLE
                }


                return false
            }

        })
    }

    fun setHistory() {
        binding.recyclerViewVideos.visibility = View.GONE

        binding.recyclerViewVideoHistory.apply {
            binding.layoutVideo.visibility = View.GONE
            visibility = View.VISIBLE
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controllerHistory = UserSearchHistoryController(context, parentFrag)
            binding.recyclerViewVideoHistory.setController(controllerHistory!!)
        }
    }

    private fun setVideosRecycler() {
        binding.recyclerViewVideos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controller =
                UserSearchVideoController(context, requireActivity(), viewModel, videoRepository)
            binding.recyclerViewVideos.setController(controller!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        controller = null
        controllerHistory = null

        _binding = null
    }
}