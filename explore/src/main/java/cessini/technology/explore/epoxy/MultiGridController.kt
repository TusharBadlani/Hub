package cessini.technology.explore.epoxy

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import cessini.technology.explore.fragment.ExploreFragmentDirections
import cessini.technology.model.roomInfo
import com.airbnb.epoxy.AsyncEpoxyController


class MultiGridController(var activity: FragmentActivity?,val fragment:Fragment,val category:String): AsyncEpoxyController() {
    var allRooms: MutableList<roomInfo> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }
    override fun buildModels() {
        Log.d("Hemlo", "I entered")
        allRooms.forEachIndexed { index, roomInfo ->
            val data = roomInfo.datas
            val size = data.size
            //initial height=308 and weight=191
            val controller = EpoxyController(154, 95,15f,category,fragment,true)
            controller.roomUsers = data
            multiGridModelRecycler {
                id("$allRooms $index")
                controller(controller)
              size(data.size)
                roomTitle(roomInfo.title)
                clickListener{ _, ->
                    var t1=""
                    Log.e("MultiGridController","category rooms on click listener called")
                    fragment.let {
                        it.findNavController().navigate(
                            ExploreFragmentDirections.actionExploreFragmentToLiveFragment(
                                "Suggestion Rooms",
                                category
                            )
                        )

                    }
                }
            }
        }
    }
}

