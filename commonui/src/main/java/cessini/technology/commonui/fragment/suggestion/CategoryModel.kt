package cessini.technology.commonui.fragment.suggestion

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cessini.technology.commonui.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.chip.ChipGroup

@EpoxyModelClass
abstract class CategoryModel : EpoxyModelWithHolder<ItemVH>()  {
    companion object {
        private const val TAG = "CategoryModel"
    }

    @EpoxyAttribute
    lateinit var subCategoryHeading : String
    @EpoxyAttribute lateinit var chipGroupOne : ChipGroup
    @EpoxyAttribute lateinit var chipGroupTwo : ChipGroup
    @EpoxyAttribute lateinit var chipGroupThree : ChipGroup

    private lateinit var adapter: Adapter
    private lateinit var recyclerView: RecyclerView



    override fun getDefaultLayout(): Int {
        return R.layout.view_holder_categories
    }


    override fun bind(view: ItemVH) {
        super.bind(view)
        setUpRecyclerView(view.recyclerView1)
        val list:List<ChipGroup> = listOf(chipGroupOne,chipGroupTwo,chipGroupThree)
        adapter.differ.submitList(list)
        view.subHeading.text = subCategoryHeading
//        (chipGroupOne.parent as? ViewGroup)?.removeView(chipGroupOne)
//        view.chipGroupOne.addView(chipGroupOne)
//        (chipGroupTwo.parent as? ViewGroup)?.removeView(chipGroupTwo)
//        view.chipGroupTwo.addView(chipGroupTwo)
//        (chipGroupThree.parent as? ViewGroup)?.removeView(chipGroupThree)
//        view.chipGroupThree.addView(chipGroupThree)
//        view.chipGroupThree.measure(50,65)
    }

    private fun setUpRecyclerView(recyclerView1: RecyclerView) {
        adapter = Adapter()
        recyclerView = recyclerView1
        recyclerView.adapter = adapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.HORIZONTAL)
        //recyclerView.setHasFixedSize(false)
    }
}
class ItemVH : KotlinEpoxyHolder(){
    val subHeading by bind<TextView>(R.id.category_heading)
//    val chipGroupOne by bind<ChipGroup>(R.id.chipGroup_one)
//    val chipGroupTwo by bind<ChipGroup>(R.id.chipGroup_two)
//    val chipGroupThree by bind<ChipGroup>(R.id.chipGroup_three)
    val recyclerView1 by bind<RecyclerView>(R.id.recyclerview)



}
