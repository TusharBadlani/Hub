package cessini.technology.explore.epoxy

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.explore.R
import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyController


@EpoxyModelClass
abstract class RecordViewPagerModel : EpoxyModelWithHolder<RecordViewPagerModel.Holder>() {


    override fun getDefaultLayout(): Int
    {

        return R.layout.view_pager_item
    }
    @EpoxyAttribute
    lateinit var categoryTitle : String

    @EpoxyAttribute
     lateinit var controller : RecordMySpaceController

    @EpoxyAttribute
    var position: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
          holder.epoxyview.setController(controller)
            holder.category.text= categoryTitle
            if(position==0){
                holder.epoxyview.translationX=25f
                holder.category.translationX=25f
            }

        }

    }

    class Holder : EpoxyHolder() {
        lateinit var epoxyview : EpoxyRecyclerView
        lateinit var category: TextView

        override fun bindView(itemView: View) {
            epoxyview= itemView.findViewById(R.id.viewpagerepoxy)
            category= itemView.findViewById(R.id.Category)



        }
    }
}
