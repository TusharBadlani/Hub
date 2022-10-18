package cessini.technology.explore.epoxy

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.marginLeft
import cessini.technology.explore.R
import com.airbnb.epoxy.*


@EpoxyModelClass
abstract class ExpertViewPagerModel : EpoxyModelWithHolder<ExpertViewPagerModel.Holder>() {

    override fun getDefaultLayout(): Int {
        return R.layout.expert_view_pager
    }

    @EpoxyAttribute
    lateinit var categoryTitle : String

    @EpoxyAttribute
    lateinit var controller : ExpertVideoController

    @EpoxyAttribute
    var position: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
            holder.epoxyview.setController(controller)
            holder.category.text= categoryTitle
            Log.e("Position","${position}")
            if(position==0) {
                holder.category.translationX=25f
                holder.epoxyview.translationX = 25f
            }
//            if(focus.visibility==View.VISIBLE){
//                focus.visibility=View.GONE
//                Log.e("FocusVisible","${position} is visible")
//            }
//            else if(focus.visibility==View.GONE){
//                focus.visibility=View.VISIBLE
//                Log.e("FocusVisible","${position} is visible")
//            }
//            else{
//                Log.e("FocusVisible","${position} is visible ELSE")
//            }

        }

    }

    class Holder : EpoxyHolder(){
        lateinit var epoxyview : EpoxyRecyclerView
        lateinit var category: TextView
        lateinit var focus:TextView

        override fun bindView(itemView: View) {
            epoxyview= itemView.findViewById(R.id.viewpagerepoxy)
            category= itemView.findViewById(R.id.Category)
            focus = itemView.findViewById(R.id.textFocus)
        }
    }
}