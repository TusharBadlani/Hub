package cessini.technology.explore.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import cessini.technology.explore.controller.UserSearchMySpaceController
import cessini.technology.explore.controller.UserSearchVideoController
import cessini.technology.explore.databinding.FragmentUserSearchMyspaceBinding
import cessini.technology.explore.databinding.FragmentUserSearchVideoBinding
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.model.Room
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.video.VideoRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserSearchMySpace : Fragment() {

    @Inject
    lateinit var roomRepository: RoomRepository
   @Inject
   lateinit var userIdentifierPreferences: UserIdentifierPreferences
    val viewModel: ExploreSearchViewModel by activityViewModels()

    private var _binding: FragmentUserSearchMyspaceBinding? = null
    val binding get() = _binding!!

    var controller: UserSearchMySpaceController? = null
    var controllerHistory: UserSearchHistoryController? = null
    lateinit var parentFrag: ExploreSearchFragment
    var history: ArrayList<SearchHistoryEntity> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_search_myspace, container, false)
        binding.roomViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.contextRoom = requireContext()
        setRoomRecycler()

        var allRoom: MutableList<Room> = mutableListOf()
        viewModel.roomResponseModels.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                controller!!.allRoom = it
                controller!!.roomFilterList = it
            }
        })


        parentFrag = this@UserSearchMySpace.parentFragment as ExploreSearchFragment

//        Toast.makeText(activity,"Myspace",Toast.LENGTH_SHORT).show()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        setHistory()
        binding.recyclerViewRoomHistory.visibility = View.VISIBLE
        viewModel.videoHistory.observe(viewLifecycleOwner, Observer {

            Log.i("Explore", "Outside: $it")
            if (it != null) {

                history = it
                Log.i("Explore", "Inside: $it")
                controllerHistory!!.historyList = it

                if (history.size == 0) {

                    binding.layoutRoom.visibility = View.VISIBLE
                    binding.recyclerViewRoomHistory.visibility = View.GONE

                } else {
                    binding.layoutRoom.visibility = View.GONE
                    binding.recyclerViewRoomHistory.visibility = View.VISIBLE
                }
            }

        })

        val parentFrag: ExploreSearchFragment =
            this@UserSearchMySpace.parentFragment as ExploreSearchFragment
        parentFrag.binding.searchViewHeader.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.recyclerViewRoomHistory.visibility = View.GONE

                Log.i("Submit searched texts: ", query!!)
                viewModel.fetchSearchRoomQueryAPI(query)
                viewModel.roomResponseModels.observe(viewLifecycleOwner, Observer {


                    if (it != null) {
                        if (it.size > 0) {
                            binding.layoutRoom.visibility = View.GONE
                            binding.recyclerViewRoom.visibility = View.VISIBLE

                        } else if (it.size == 0) {
                            binding.layoutRoom.visibility = View.GONE
                            binding.recyclerViewRoom.visibility = View.GONE
                        }
                    }

                    binding.recyclerViewRoomHistory.visibility = View.GONE
                })

                if (query == "") {
                    binding.layoutRoom.visibility = View.GONE
                    binding.recyclerViewRoom.visibility = View.GONE
                    binding.recyclerViewRoomHistory.visibility = View.VISIBLE
                }

                //Insert to db
                val entity =
                    SearchHistoryEntity(id = "$query Room", category = "Room", query = query)
                viewModel.insertSearchQueryToDB(entity)
                Log.d("Explore", entity.toString())

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.i("Video text pattern: ", newText!!)

                viewModel.roomResponseModels.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        if (it.size > 0) {
                            binding.layoutRoom.visibility = View.GONE
                            binding.recyclerViewRoom.visibility = View.VISIBLE
                            binding.layoutNoResultRoom.visibility = View.GONE
                        } else if (it.size == 0) {
                            binding.layoutRoom.visibility = View.GONE
                            binding.recyclerViewRoom.visibility = View.GONE
                            binding.layoutNoResultRoom.visibility = View.VISIBLE
                        }
                    }

                })

                if (newText == "") {
                    binding.recyclerViewRoomHistory.visibility = View.VISIBLE
                    binding.recyclerViewRoom.visibility = View.GONE
                    binding.layoutNoResultRoom.visibility = View.GONE
                }

                if (newText.length == 2 || newText.length > 2) {
                    binding.recyclerViewRoomHistory.visibility = View.GONE
                    viewModel.fetchSearchRoomQueryAPI(newText)
                    binding.recyclerViewRoom.visibility = View.VISIBLE
                }


                return false
            }

        })
    }

    fun setHistory() {
        binding.recyclerViewRoom.visibility = View.GONE

        binding.recyclerViewRoomHistory.apply {
            binding.layoutRoom.visibility = View.GONE
            visibility = View.VISIBLE
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controllerHistory = UserSearchHistoryController(context, parentFrag)
            binding.recyclerViewRoomHistory.setController(controllerHistory!!)
        }
    }

    private fun setRoomRecycler() {
        binding.recyclerViewRoom.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controller =
                UserSearchMySpaceController(context, requireActivity(), this@UserSearchMySpace,viewModel, roomRepository,userIdentifierPreferences)
            setController(controller!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        controller = null
        controllerHistory = null

        _binding = null
    }
}