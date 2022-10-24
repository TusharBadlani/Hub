package cessini.technology.explore.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.explore.R
import cessini.technology.explore.adapter.SearchTabviewAdopter
import cessini.technology.explore.databinding.FragmentExploreSearchBinding
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreSearchFragment : BaseFragment<FragmentExploreSearchBinding>(R.layout.fragment_explore_search) {

    val viewModel: ExploreSearchViewModel by activityViewModels()

//    private val navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.activity = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.context = this

        binding.viewpager.currentItem = viewModel.viewPagerCounter.value!!

        viewModel.viewPagerCounter.observe(viewLifecycleOwner, Observer {
            binding.viewpager.currentItem = viewModel.viewPagerCounter.value!!
        })

        init()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.searchViewHeader.setQuery("", false)
                binding.searchViewHeader.queryHint = "Search ${tab?.text}"
                viewModel.tabPosition = tab!!.position

                Log.i("Tab position", viewModel.tabPosition.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun init() {
        // removing toolbar elevation
        binding.viewpager.adapter =
                SearchTabviewAdopter(viewModel.titles.value!!, childFragmentManager, lifecycle)

        // attaching tab mediator
        TabLayoutMediator(
                binding.tabLayout, binding.viewpager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = viewModel.titles.value!![position]
            binding.searchViewHeader.setQuery("", false)
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        binding.searchViewHeader.queryHint = "Search hub"

        /** Navigating user back to Explore Section. */
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }


    }

//    override fun navigateToFlow(flow: NavigationFlow) {
//        navigator.navigateToFlow(flow)
//    }
}
