package cessini.technology.explore.epoxy

import android.content.Context
import android.util.Log
import cessini.technology.model.MessageI
import com.airbnb.epoxy.AsyncEpoxyController

class InfoController() : AsyncEpoxyController()  {
    var items: List<MessageI> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }
    private val CORNER_RADIUS_MAX: Float = 20f
    override fun buildModels() {
        var i=0;
        var size = items.size
        items.forEachIndexed { index, it ->
            info {
                Log.d("InfoModel",it.toString())
                id(index)
                img(it.image)
                title1(it.title)
                description1(it.description)
                view1(it.views)
            }
        }
    }
}