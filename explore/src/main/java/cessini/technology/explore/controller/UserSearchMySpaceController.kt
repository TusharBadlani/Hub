package cessini.technology.explore.controller

import android.content.Context
import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import  cessini.technology.explore.epoxy.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
//import cessini.technology.commonui.fragment.suggestion.categories
import cessini.technology.explore.*
import cessini.technology.explore.fragment.ExploreSearchFragmentDirections
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.model.Room
import cessini.technology.model.RoomUsers
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import com.airbnb.epoxy.AsyncEpoxyController
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class   UserSearchMySpaceController(
    var context: Context,
    val activity: FragmentActivity,
    val fragment:Fragment,
    val viewModel: ExploreSearchViewModel,
    private val roomRepository: RoomRepository,
    private val userIdentifierPreferences: UserIdentifierPreferences
) : AsyncEpoxyController(), Filterable {

    var allRoom: MutableList<Room> = mutableListOf()


    var roomFilterList = ArrayList<Room>()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        roomFilterList.forEachIndexed { index, room ->
            searchMyspace {
                id(room.id.hashCode())
                creatorName(room.creator.name)
                time(room.time)
                live(room.live)
                val gridList: MutableList<RoomUsers> = mutableListOf()
                room.creator.let {
                    gridList.add(RoomUsers(it.id,room.title,it.name,it.profilePicture,it.channelName))

                }
                room.listeners = room.listeners.toSet().toList()
                for(i in 0..room.listeners.size-1){
                    if(i>=3)
                        break
                    var it = room.listeners.get(i)
                    gridList.add(RoomUsers(it._id,room.title,it.name,it.profile_picture,it.channel_name))
                }
//               room.listeners.forEach {
//                   gridList.add(RoomUsers(it._id,room.title,it.name,it.profile_picture,it.channel_name))
//               }
                var layoutManager: GridLayoutManager = GridLayoutManager(fragment.context, 2)
                val size= gridList.size
                if (size <= 2)
                    layoutManager = GridLayoutManager(fragment.context, 1)
                else if (size % 2 != 0) {
                    layoutManager = GridLayoutManager(fragment.context, 2)

                    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            when (position) {
                                0 -> return 2
                                else -> return 1
                            }
                        }
                    }
                }
                layoutManager(layoutManager)
                val controller = EpoxyController(115,65,5f,"",fragment,false)
                controller.roomUsers= gridList
                controller(controller)

                var text = ""
                var categorytext = ""

                if (room.categories.isNotEmpty()) {
                    val category = StringBuilder()
                    room.categories.forEach {
                        category.append(categories[it.toInt()] + "#")
                    }
                    val fh = category.indexOf("#")
                    val sh = category.indexOf("#", fh + 1)
                    val th = category.indexOf("#", sh + 1)
                    categorytext += "#" + category.substring(1, fh) + " "
                    if (sh != -1) {
                        categorytext += "#" + category.substring(fh + 2, sh) + " "

                    } else

                        if (th != -1) {
                            categorytext += "#" + category.substring(sh + 2, th) + " "
                        }

                }
                text = context.getString(R.string.some_text, room.title, categorytext)
                span(HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))
                onClick { _ ->
                    if(room.live)
                    {
                        fragment.findNavController().navigate(ExploreSearchFragmentDirections.actionExploreSearchFragmentToLiveFragment("Trending Rooms",""))
                    }
                    else {
                        (activity as ToFlowNavigable).navigateToFlow(
                            NavigationFlow.AccessRoomFlow(
                                room.name
                            )
                        )
                    }
                }
                onJoin { view ->
                    if (!userIdentifierPreferences.loggedIn) {
                        (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.AuthFlow)

                    } else {
                        activity.lifecycleScope.launch {
                            runCatching { roomRepository.joinRoom(room.name) }
                                .onSuccess {
                                    Toast.makeText(
                                        activity,
                                        "Join request sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view.setBackgroundResource(R.drawable.round_enable_viewbutton)
                                }
                        }
                    }
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    roomFilterList = ArrayList(allRoom)
                } else {
                    val resultList = ArrayList<Room>()
                    for (row in allRoom) {
                        val query = row.title
                        if (query!!.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    roomFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = roomFilterList
                Log.i("Room Filter List", roomFilterList.toString())
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                roomFilterList = results?.values as ArrayList<Room>
                Log.i("Videos Filter List", roomFilterList.toString())
                requestModelBuild()
            }

        }
    }

    val categories = listOf(
        " Film and animation ",
        " Cars and vehicles ",
        " Music ",
        " Pets and animals ",
        " Sport ",
        " Travel and events ",
        " Gaming ",
        " People and blogs ",
        " Comedy ",
        " Entertainment ",
        " News and politics ",
        " How-to and style ",
        " Education ",
        " Science and technology ",
        " Non-profits and activism ",
    )
}
