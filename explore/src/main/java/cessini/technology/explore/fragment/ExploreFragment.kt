package cessini.technology.explore.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseFragment
import cessini.technology.explore.R
import cessini.technology.explore.controller.MainRecyclerViewController
import cessini.technology.explore.databinding.FragmentSearchBinding
import cessini.technology.explore.states.ExploreOnClickEvents
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.explore.viewmodel.SearchViewModel
import cessini.technology.model.*
import cessini.technology.model.recordmyspacegrid.recordGrid
import cessini.technology.model.recordmyspacegrid.viewpagerItem
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.EpoxyVisibilityTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import uz.jamshid.library.progress_bar.CircleProgressBar
import javax.inject.Inject


@AndroidEntryPoint
@ExperimentalPagingApi
class ExploreFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    companion object {
        private const val TAG = "ExploreFragment"
    }

    val viewModel: SearchViewModel by activityViewModels()
    var controller: MainRecyclerViewController? = null

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    @Inject
    lateinit var roomRepository: RoomRepository

    val videoViewModel: ExploreSearchViewModel by activityViewModels()

    private lateinit var allCategory: Explore

    private var y: Int = 0

    private var isConstrainedToAppBar: Boolean = true

    private var statusBarHeight: Int = 0

    private lateinit var trendingRooms: TrendingRooms

    private var items: MutableSet<List<HealthFitness>?> = mutableSetOf()
    private var suggestedRooms: MutableList<SuggestionCategoryRooms> = mutableListOf()
    private var infoItems: MutableSet<List<MessageI>> = mutableSetOf()
    private var recordMyspaceRooms: MutableList<viewpagerItem> = mutableListOf()
    private var recordCommonMyspaceRooms: MutableList<viewpagerItem> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showShimmer()
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d(TAG, "On Create Called")

        if (userIdentifierPreferences.loggedIn) {
            (activity as HomeActivity).profileDrawable?.let {
                (activity as HomeActivity).setUpNavProfileIcon(
                    null, it, false
                )
            }
        } else {
            (activity as HomeActivity).profileDrawable = null
        }

        setMainCategoryRecycler()

        (activity as HomeActivity).setSupportActionBar(binding.toolbar)

        //setting SwipeRefreshLayout
        binding.swipeRefreshLayout.apply {
            val cp = CircleProgressBar(requireContext())
            cp.setBorderWidth(2)
            binding.swipeRefreshLayout.setCustomBar(cp)

            setRefreshListener {
                showShimmer()
                viewModel.fetchCategoriesAPI()
                viewModel.fetchLiveRoomsTest()
                viewModel.fetchAllComponent()
                viewModel.fetchAllInfo()
                viewModel.fetchSuggestedRooms()
                viewModel.fetchAllTrendingRooms()
                viewModel.fetchAllRecordedVideos()
                viewModel.fetchAllCommonRecordedVideos()
                viewModel.getFollowing()
                Handler(Looper.getMainLooper()).postDelayed({

                    try {
                        binding.swipeRefreshLayout.setRefreshing(false)
                    } catch (e: NullPointerException) {
                        //empty catch block
                    }
                }, 3000)
            }

            // Attach visibility tracker to the RecyclerView to enable visibility events.
            EpoxyVisibilityTracker().attach(recyclerViewParent)
            Carousel.setDefaultGlobalSnapHelperFactory(object : Carousel.SnapHelperFactory() {
                override fun buildSnapHelper(context: Context?): SnapHelper {
                    return PagerSnapHelper()
                }
            })

        }
        addTrendingRooms()
    }

    override fun onResume() {
        super.onResume()
        /**Hide the keyboard.*/
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
        binding.swipeRefreshLayout.isEnabled =
            binding.recyclerViewParent.layoutManager?.findViewByPosition(0)?.top == 0

    }
    private fun setMainCategoryRecycler() {
    binding.recyclerViewParent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            y = dy
            if (dy > 0) {
                toolbar.visibility = View.GONE


            } else {
                toolbar.visibility = View.VISIBLE
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val layoutManager = recyclerView.layoutManager
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (y < 0) {
                    toolbar.visibility = View.VISIBLE
                } else {
                    toolbar.visibility = View.GONE
                }
            }
            if (recyclerView.canScrollVertically(0) && newState == RecyclerView.SCROLL_STATE_IDLE && layoutManager?.findViewByPosition(
                    0
                )?.top == 0
            ) {
                val constrainLayout = binding.searchCL
                val constraintSet = ConstraintSet()
                constraintSet.clone(constrainLayout)
                constraintSet.connect(
                    R.id.swipeRefreshLayout,
                    ConstraintSet.TOP,
                    R.id.appbar,
                    ConstraintSet.BOTTOM,
                    0
                )
                constraintSet.applyTo(constrainLayout)
                binding.swipeRefreshLayout.isEnabled = true
            } else {
                val constrainLayout = binding.searchCL
                val constraintSet = ConstraintSet()
                constraintSet.clone(constrainLayout)
                constraintSet.connect(
                    R.id.swipeRefreshLayout,
                    ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP,
                    statusBarHeight
                )
                constraintSet.applyTo(constrainLayout)
                binding.swipeRefreshLayout.isEnabled = false
            }
        }
    })


    binding.recyclerViewParent.apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        controller = MainRecyclerViewController(
            context,
            viewModel,
            requireActivity(),
            this@ExploreFragment,
            roomRepository,
            userIdentifierPreferences,
            videoViewModel,
        ) { onItemClick ->
            when (onItemClick) {
                is ExploreOnClickEvents.ToAccessRoomFlow -> {
                    /*  Click Event of UpcomingHub Section items to AccessRoomFlow */
                    (requireActivity() as ToFlowNavigable).navigateToFlow(
                        NavigationFlow.AccessRoomFlow(
                            onItemClick.room.name
                        )
                    )
                }
                is ExploreOnClickEvents.ExploreFragmentToLiveFragment -> {
                    findNavController().navigate(
                        ExploreFragmentDirections.actionExploreFragmentToLiveFragment(
                            onItemClick.type,
                            onItemClick.type1
                        )
                    )
                }
                is ExploreOnClickEvents.ToGlobalProfileFlow -> {
                    /*  Click Event of Voices to Follow Profile Image */
                    /**we will navigate to the global public profile.*/
                    (requireActivity() as ToFlowNavigable).navigateToFlow(
                        NavigationFlow.GlobalProfileFlow
                    )
                }
                is ExploreOnClickEvents.ShareEvent -> {
                    /*  Click Event for Sharing */
                    val data = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(
                            Intent.EXTRA_TEXT,
                            "Let's talk with best creators who help you on Myworld! " +
                                    "It's a fast, simple and secure app. " +
                                    "Get it at https://play.google.com/store/apps/details?id=cessini.technology.myworld"
                        )
                        this.type = "text/plain"
                    }
                    this@ExploreFragment.startActivity(data)
                }
                is ExploreOnClickEvents.RefreshFeed->{
                    recyclerViewParent.smoothScrollToPosition(0)
                }
            }

        }
//            setController(controller!!).also {
//                it.setData(ViewPagerData(resources.getStringArray(R.array.view_pager_items), 0))
//            }

//            var item : MutableList<List<HealthFitness>?> = mutableListOf(
//                viewModel.allComponent.value?.message?.trendingTechnology,
//                viewModel.newDetails.message?.entertainment,
//                viewModel.newDetails.message?.healthFitness,
//                viewModel.newDetails.message?.knowledgeCareers,
//                viewModel.newDetails.message?.trendingNews
//
//            )


        addItems()
        addInfoItems()
        addSuggestedRooms()
        addTrendingRooms()



        viewModel.allCategory.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "setMainCategoryRecycler AllCategory: $it")

            allCategory = it
            if (allCategory.categoryRooms.isEmpty()) {
                addSuggestedRooms()
                allCategory.categoryRooms = suggestedRooms
            }
            if (allCategory.recordMyspcaeGrid.isEmpty()) {
                addrecordMySpaceItems()
                allCategory.recordMyspcaeGrid = recordMyspaceRooms
            }
            if (allCategory.recordCommonMyspcaeGrid.isEmpty()) {
                addrecordCommonMySpaceItems()
                allCategory.recordCommonMyspcaeGrid = recordCommonMyspaceRooms
            }
            // Labeling items which is first and which is last
            it.videos.forEachIndexed { _, pair ->
                pair.second.forEachIndexed { index, video ->
                    when (index) {
                        0 -> video.positionType = 0
                        pair.second.size - 1 -> video.positionType = 2
                        else -> video.positionType = 1
                    }
                }
            }
            //hideShimmer()

        })


        viewModel.liveRooms.observe(viewLifecycleOwner, Observer {

            Log.d(TAG, "setMainCategoryRecycler Not paging: $it")

            if (it != null) {
                allCategory.liveRooms = it
            }
//                controller!!.allCategory = allCategory
            allCategory.items = items
            addSuggestedRooms()
            allCategory.categoryRooms = suggestedRooms
            allCategory.itemsInfo = infoItems
            allCategory.visibleItemIndex = 0
            allCategory.trendingRooms = mutableListOf(trendingRooms.message.toMutableList())
            recyclerViewParent.setController(controller!!.also { mainRecyclerViewController->
                mainRecyclerViewController.setData(allCategory)
            })
            adapter = controller!!.adapter
            hideShimmer()
        })
//            viewModel.suggestedroomresponse.observe(viewLifecycleOwner,
//                {
//                    allCategory.categoryRooms=it
//                    recyclerViewParent.setController(controller!!.also {
//                        it.setData(allCategory)
//                    })
//                    adapter = controller!!.adapter
//                })

    }

    lifecycleScope.launchWhenStarted {
        viewModel.liveRoomsTest.collect {

        }
    }
}

    private fun addSuggestedRooms() {
        viewModel.suggestedroomresponse.observe(
            viewLifecycleOwner
        ) {
            //hideShimmer()
            suggestedRooms = it
            Log.d("Hemlo", suggestedRooms.toString())
        }
    }

    private fun addTrendingRooms() {
        viewModel.allTrendingRooms.observe(viewLifecycleOwner, Observer {
            trendingRooms = it

            Log.e("SettingTrendingRoomEF",it.toString())
        })
    }

    private fun addrecordMySpaceItems() {
        val viewpageritems = viewModel.allRecordVideo.value
//    val img1= "https://ibb.co/zPcKrQ2.jpg"
//    val img2="https://new-myworld-bucket.s3.amazonaws.com/user_files/ID885914130000000082/profile/picture1641802910569.jpg"
//    val img3="https://new-myworld-bucket.s3.amazonaws.com/user_files/ID525455520000000081/profile/picture1641631430989.jpg"
//    val list1 :MutableList<recordGrid> = mutableListOf()
//    list1.add(recordGrid(img1,"Sample","Atishay","Tech","Science"))
//    list1.add(recordGrid(img2,"Sample2","Atishay","Tech2","Science2"))
//    list1.add(recordGrid(img3,"Sample3","Atishay","Tech3","Science3"))
        var itemTrendingTechnology: MutableList<recordGrid> = mutableListOf()
        var itemTrendingNews: MutableList<recordGrid> = mutableListOf()
        var itemTrendingScience: MutableList<recordGrid> = mutableListOf()
        var itemHealthFitness: MutableList<recordGrid> = mutableListOf()
        var itemEntertainment: MutableList<recordGrid> = mutableListOf()
        viewpageritems?.forEach { item ->
            if (item.category.equals("Trending Technology")) {
                itemTrendingTechnology.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Trending News")) {
                itemTrendingNews.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Science and Tech")) {
                itemTrendingScience.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Health and Fitness")) {
                itemHealthFitness.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Entertainment")) {
                itemEntertainment.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
        }
//    val item1= viewpagerItem("Trending Technology",itemTrendingTechnology)
//    val item2= viewpagerItem("Trending News",itemTrendingNews)
//    val item3= viewpagerItem("Science and Tech",itemTrendingScience)
//    val item4= viewpagerItem("Health & Fitness",itemHealthFitness)
//    val item5= viewpagerItem("Entertainment",itemEntertainment)
        val viewpagerlist = mutableListOf<viewpagerItem>()
        if (itemTrendingTechnology.size > 0) {
            viewpagerlist.add(viewpagerItem("Trending Technology", itemTrendingTechnology))
        }
        if (itemTrendingNews.size > 0) {
            viewpagerlist.add(viewpagerItem("Trending News", itemTrendingNews))
        }
        if (itemTrendingScience.size > 0) {
            viewpagerlist.add(viewpagerItem("Science and Tech", itemTrendingScience))
        }
        if (itemHealthFitness.size > 0) {
            viewpagerlist.add(viewpagerItem("Health & Fitness", itemHealthFitness))
        }
        if (itemEntertainment.size > 0) {
            viewpagerlist.add(viewpagerItem("Entertainment", itemEntertainment))
        }
        recordMyspaceRooms = viewpagerlist
    }

    private fun addrecordCommonMySpaceItems() {
        val viewpageritems = viewModel.allCommonRecordVideo.value

        var itemTrendingTechnology: MutableList<recordGrid> = mutableListOf()
        var itemTrendingNews: MutableList<recordGrid> = mutableListOf()
        var itemTrendingScience: MutableList<recordGrid> = mutableListOf()
        var itemHealthFitness: MutableList<recordGrid> = mutableListOf()
        var itemEntertainment: MutableList<recordGrid> = mutableListOf()
        viewpageritems?.forEach { item ->
            if (item.category.equals("Trending Technology")) {
                itemTrendingTechnology.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Trending News")) {
                itemTrendingNews.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Science and Tech")) {
                itemTrendingScience.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Health and Fitness")) {
                itemHealthFitness.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
            if (item.category.equals("Entertainment")) {
                itemEntertainment.add(
                    recordGrid(
                        item.thumbnail,
                        item.title,
                        item.username,
                        item.category,
                        ""
                    )
                )
            }
        }

        val viewpagerlist = mutableListOf<viewpagerItem>()
        if (itemTrendingTechnology.size > 0) {
            viewpagerlist.add(viewpagerItem("Trending Technology", itemTrendingTechnology))
        }
        if (itemTrendingNews.size > 0) {
            viewpagerlist.add(viewpagerItem("Trending News", itemTrendingNews))
        }
        if (itemTrendingScience.size > 0) {
            viewpagerlist.add(viewpagerItem("Science and Tech", itemTrendingScience))
        }
        if (itemHealthFitness.size > 0) {
            viewpagerlist.add(viewpagerItem("Health & Fitness", itemHealthFitness))
        }
        if (itemEntertainment.size > 0) {
            viewpagerlist.add(viewpagerItem("Entertainment", itemEntertainment))
        }
        recordCommonMyspaceRooms = viewpagerlist
    }

    private fun addInfoItems() {
        val info = viewModel.allInfo.value?.message
        //Dummy data to check functionality
//    val info:MutableList<MessageI> = mutableListOf()
//    if(info1!=null&& info1.size>0) {
//      for(i in 0..9)
//      info.add(info1.get(0))
//    }
        if (info != null && info.size > 0) {

            var i = 0
            while (i < info.size) {
                if (i + 2 < info.size) {

                    infoItems.add(listOf(info.get(i), info.get(i + 1), info.get(i + 2)))
                    i += 3
                    continue
                } else if (i + 1 < info.size) {

                    infoItems.add(listOf(info.get(i), info.get(i + 1)))
                    i += 3
                    continue
                } else {

                    infoItems.add(listOf(info.get(i)))
                    i += 3
                    continue
                }
            }

        }
    }

    private fun addItems() {
        if (viewModel.allComponent.value != null) {
            if (viewModel.allComponent.value?.message?.trendingTechnology != null && viewModel.allComponent.value?.message?.trendingTechnology?.size!! > 0)
                items.add(viewModel.allComponent.value?.message?.trendingTechnology)

            if (viewModel.allComponent.value?.message?.entertainment != null && viewModel.allComponent.value?.message?.entertainment?.size!! > 0)
                items.add(viewModel.allComponent.value?.message?.entertainment)

            if (viewModel.allComponent.value?.message?.healthFitness != null && viewModel.allComponent.value?.message?.healthFitness?.size!! > 0)
                items.add(viewModel.allComponent.value?.message?.healthFitness)

            if (viewModel.allComponent.value?.message?.knowledgeCareers != null && viewModel.allComponent.value?.message?.knowledgeCareers?.size!! > 0)
                items.add(viewModel.allComponent.value?.message?.knowledgeCareers)

            if (viewModel.allComponent.value?.message?.trendingNews != null && viewModel.allComponent.value?.message?.trendingNews?.size!! > 0)
                items.add(viewModel.allComponent.value?.message?.trendingNews)
        }
        //Log.e("SettingItems",items.toString())
    }

    private fun showShimmer() {
        Log.e("Shimmer", "Show shimmer called")
        binding.exploreShimmer.visibility = View.VISIBLE
        binding.recyclerViewParent.visibility = View.GONE
    }

    private fun hideShimmer() {
        Log.e("Shimmer", "Hide shimmer called")
        binding.exploreShimmer.visibility = View.GONE
        binding.recyclerViewParent.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()

        controller?.context = null
        controller?.activity = null
        controller?.viewModel = null

        controller = null
    }

}
