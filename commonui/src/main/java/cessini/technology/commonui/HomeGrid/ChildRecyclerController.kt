package cessini.technology.commonui.HomeGrid

import android.content.Context
import android.util.Log
import cessini.technology.commonui.R
import cessini.technology.model.Viewer
import com.airbnb.epoxy.AsyncEpoxyController


class ChildRecyclerController(var context: Context) : AsyncEpoxyController() {

    var viewerItems: List<Viewer> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        for (i in 0..viewerItems.size-1) {
            Log.d("child","viwere size: ${viewerItems.size}")
            Log.d("child","viewer: ${viewerItems[i].caption}")
            story {
                id(i)
                context(context)
                imageUrl(viewerItems[i].thumbnail)
            }
        }
    }
}