package cessini.technology.myspace.create

import android.content.Context
import android.util.Log
import cessini.technology.commonui.fragment.suggestion.category
import cessini.technology.model.ListCategory
import com.airbnb.epoxy.TypedEpoxyController
import java.lang.RuntimeException

class SubCategoryViewHolder(val context: Context): TypedEpoxyController<ArrayList<ListCategory>>() {
    companion object {
        private const val TAG = "SubcategoryViewHolder"
    }

    override fun onExceptionSwallowed(exception: RuntimeException) {
        super.onExceptionSwallowed(exception)
        Log.d(TAG,"exception is $exception")
    }

    override fun buildModels(data: ArrayList<ListCategory>?) {
        data?.forEach {
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