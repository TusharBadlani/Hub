package cessini.technology.commonui.epoxy.suggestion

import android.content.Context
import android.util.Log
import cessini.technology.model.ListCategory
import com.airbnb.epoxy.TypedEpoxyController

class SubCategoryViewHolderController(val context: Context) : TypedEpoxyController<List<ListCategory>>() {

    companion object {
        private const val TAG = "SubcategoryController"
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        super.onExceptionSwallowed(exception)
        Log.d(TAG,"exception is $exception")
    }

    override fun buildModels(data: List<ListCategory>) {
        data.forEach {
            category {
                id(hashCode())
                subCategoryHeading(it.category)
                chipGroupOne(it.listFirstChipGroup)
                chipGroupTwo(it.listSecondChipGroup)
                chipGroupThree(it.listThrdChipGroup)
            }
        }
    }
}

