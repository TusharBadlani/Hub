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
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.explore.R
import cessini.technology.explore.controller.UserSearchCreatorController
import cessini.technology.explore.controller.UserSearchHistoryController
import cessini.technology.explore.databinding.FragmentUserSearchCreatoBinding
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSearchCreator : Fragment() {
    private var _binding: FragmentUserSearchCreatoBinding? = null
    val binding get() = _binding!!

    var controllerHistory: UserSearchHistoryController? = null
    var controller: UserSearchCreatorController? = null
    var history: ArrayList<SearchHistoryEntity> = arrayListOf()
    lateinit var parentFrag: ExploreSearchFragment
    val viewModel: ExploreSearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_search_creato,
            container,
            false
        )
        binding.creatorViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.contextCreator = requireContext()

        setCreatorRecycler()
        viewModel.creatorResponseModels.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                controller!!.allCreators = it
            }
        })

        parentFrag = this@UserSearchCreator.parentFragment as ExploreSearchFragment

        return binding.root
    }

    private fun setCreatorRecycler() {
        binding.recyclerViewCreator.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controller = UserSearchCreatorController(context, activity)
            binding.recyclerViewCreator.setController(controller!!)

        }
    }

    override fun onResume() {
        super.onResume()

        setHistory()
        binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
        viewModel.creatorHistory.observe(viewLifecycleOwner, Observer {

            Log.i("Explore", "Outside: $it")
            if (it != null) {

                history = it
                Log.i("Explore", "Inside: $it")
                controllerHistory!!.historyList = it

                if (history.size == 0) {

                    binding.layoutCreator.visibility = View.VISIBLE
                    binding.recyclerViewCreatorHistory.visibility = View.GONE

                } else {
                    binding.layoutCreator.visibility = View.GONE
//                    binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                }
            }

        })

        val parentFrag: ExploreSearchFragment =
            this@UserSearchCreator.parentFragment as ExploreSearchFragment

        parentFrag.binding.searchViewHeader.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                binding.recyclerViewCreatorHistory.visibility = View.GONE

                Log.i("Submit searched query: ", query!!)
                viewModel.fetchSearchCreatorQueryAPI(query)
                viewModel.creatorResponseModels.observe(viewLifecycleOwner, Observer {


                    if (it != null) {
                        if (it.size > 0) {
                            binding.layoutCreator.visibility = View.GONE
                            binding.recyclerViewCreator.visibility = View.VISIBLE
                        } else if (it.size == 0) {
                            binding.layoutCreator.visibility = View.GONE
                            binding.recyclerViewCreator.visibility = View.GONE
                        }
                    }


                    binding.recyclerViewCreatorHistory.visibility = View.GONE

                })

                if (query == "") {
                    binding.layoutCreator.visibility = View.GONE
                    binding.recyclerViewCreator.visibility = View.GONE
                    binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                }

                //Insert to db
                val entity = SearchHistoryEntity(
                    id = "$query Creator",
                    category = "Creator",
                    query = query
                )
                viewModel.insertSearchQueryToDB(entity)

                Log.d("Explore", entity.toString())

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.i("Creator text pattern: ", newText!!)

                viewModel.creatorResponseModels.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        if (it.size > 0) {
                            binding.layoutCreator.visibility = View.GONE
                            binding.recyclerViewCreator.visibility = View.VISIBLE
                            binding.layoutNoResultCreator.visibility = View.GONE
                        } else if (it.size == 0) {
                            binding.layoutCreator.visibility = View.GONE
                            binding.recyclerViewCreator.visibility = View.GONE
                            binding.layoutNoResultCreator.visibility = View.VISIBLE
                        }
                    }
                })

                if (newText.isEmpty()) {
                    binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                    binding.recyclerViewCreator.visibility = View.GONE
                    binding.layoutNoResultCreator.visibility = View.GONE
                }

                if (newText.length == 2 || newText.length > 2) {
                    viewModel.fetchSearchCreatorQueryAPI(newText)
                    binding.recyclerViewCreatorHistory.visibility = View.GONE
                    binding.recyclerViewCreator.visibility = View.VISIBLE
                }

                return false
            }

        })
    }

    fun setHistory() {
        binding.recyclerViewCreator.visibility = View.GONE
        binding.recyclerViewCreatorHistory.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            controllerHistory = UserSearchHistoryController(context, parentFrag)
            binding.recyclerViewCreatorHistory.setController(controllerHistory!!)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()

        controller = null
        controllerHistory = null
        _binding = null
    }
}
