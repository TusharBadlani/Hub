package cessini.technology.myspace.create

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cessini.technology.commonui.fragment.suggestion.Adapter
import cessini.technology.myspace.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.chip.ChipGroup

@EpoxyModelClass
abstract class MySpaceCategoryModel : EpoxyModelWithHolder<ItemVH>(){

    @EpoxyAttribute
    lateinit var subCategoryHeading : String
    @EpoxyAttribute
    lateinit var chipGroupOne : ChipGroup
    @EpoxyAttribute
    lateinit var chipGroupTwo : ChipGroup
    @EpoxyAttribute
    lateinit var chipGroupThree : ChipGroup

    private lateinit var adapter: Adapter
    private lateinit var recyclerView: RecyclerView



    override fun getDefaultLayout(): Int {
        return R.layout.myspace_view_holder_categories
    }


    override fun bind(view: ItemVH) {
        super.bind(view)
        setUpRecyclerView(view.recyclerView1)
        val list:List<ChipGroup> = listOf(chipGroupOne,chipGroupTwo,chipGroupThree)
        adapter.differ.submitList(list)
        view.subHeading.text = subCategoryHeading

    }

    private fun setUpRecyclerView(recyclerView1: RecyclerView) {
        adapter = Adapter()
        recyclerView = recyclerView1
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        recyclerView.setHasFixedSize(false)
    }
}
class ItemVH : MySpaceEpoxyHolder(){
    val subHeading by bind<TextView>(R.id.category_heading)
    val recyclerView1 by bind<RecyclerView>(cessini.technology.commonui.R.id.recyclerview)
}