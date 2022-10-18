package cessini.technology.explore.epoxy

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.model.recordmyspacegrid.recordGrid
import com.airbnb.epoxy.AsyncEpoxyController

class RecordMySpaceController(val context: Context?) : AsyncEpoxyController() {
    var recordGrids: MutableList<recordGrid> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    private val CORNER_RADIUS_MAX: Float = 20f
    override fun buildModels() {
        Log.d("Hemlo", "I entered 2")
        var i = 0
        val size = recordGrids.size
        Log.d("Hemlo", "my size is $size")
        recordGrids.forEachIndexed { index, it ->
            recordGrid {
                id("recordgridvalue ${index}")
               gridimgId(it.gridImage)
                category1(it.category)
                title1(it.title)
                subcategory1(it.hostname)
            }


        }
    }
}