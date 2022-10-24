package cessini.technology.notifications

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.commonui.presentation.globalviewmodels.BaseViewModel
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.notifications.controller.UserSearchController
import cessini.technology.notifications.databinding.FragmentUserSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserSearchFragment : Fragment(), UserSearchController.AdapterCallbacks {

    private val viewModel: ExploreSearchViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentUserSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var controller: UserSearchController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_search, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun setCreatorRecycler() {
        binding.recyclerViewCreator.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            controller = UserSearchController(this@UserSearchFragment)
            binding.recyclerViewCreator.setController(controller)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.contextCreator = requireContext()

        setCreatorRecycler()

        viewModel.creatorResponseModels.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                controller.allCreatorsExceptCurrentUser = emptyList()
                return@observe
            }
            controller.allCreatorsExceptCurrentUser =
                list.filter {
                    it.id != baseViewModel.id.value
                }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchViewHeader.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

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


                    //binding.recyclerViewCreatorHistory.visibility = View.GONE

                })

                if (query == "") {
                    binding.layoutCreator.visibility = View.GONE
                    binding.recyclerViewCreator.visibility = View.GONE
                    //binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                }

                //Insert to db
//                val entity = SearchHistoryEntity(
//                    id = "$query Creator",
//                    category = "Creator",
//                    query = query
//                )
//                viewModel.insertSearchQueryToDB(entity)


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
                    //binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                    binding.recyclerViewCreator.visibility = View.GONE
                    binding.layoutNoResultCreator.visibility = View.GONE
                }

                if (newText.length == 2 || newText.length > 2) {
                    viewModel.fetchSearchCreatorQueryAPI(newText)
                    //binding.recyclerViewCreatorHistory.visibility = View.GONE
                    binding.recyclerViewCreator.visibility = View.VISIBLE
                }

                return false
            }

        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserSearchItemClicked(id: String, channelName: String) {
        (requireActivity() as ToFlowNavigable).navigateToFlow(
            NavigationFlow.GlobalMessageFlow(
                id, channelName
            )
        )
    }
}