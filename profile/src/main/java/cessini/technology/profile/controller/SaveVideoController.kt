package cessini.technology.profile.controller

import android.content.Context
import cessini.technology.model.recordmyspacegrid.recordGrid
import com.airbnb.epoxy.AsyncEpoxyController

class SaveVideoController(val context: Context) : AsyncEpoxyController() {
    var recordGrids: MutableList<recordGrid> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    override fun buildModels() {
        recordGrids.forEachIndexed { index, it ->
            saveVideo {
                id(index)
                gridimgId(it.gridImage)
                category1(it.category)
                title1(it.title)
                subcategory1(it.hostname)
            }
        }
    }
}