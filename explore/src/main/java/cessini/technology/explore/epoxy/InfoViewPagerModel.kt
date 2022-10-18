package cessini.technology.explore.epoxy

import android.app.Activity
import android.view.View
import android.widget.TextView
import cessini.technology.explore.R
import com.airbnb.epoxy.*
import org.w3c.dom.Text

@EpoxyModelClass
abstract class InfoViewPagerModel: EpoxyModelWithHolder<InfoViewPagerModel.Holder>() {
    override fun getDefaultLayout(): Int {
        return R.layout.info_view_pager
    }

    @EpoxyAttribute
    lateinit var controller : InfoController

    @EpoxyAttribute
    var position: Int = 0

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
            holder.epoxyview.setController(controller)
            if(position==0){
                holder.epoxyview.translationX=25f
                holder.categogy.translationX=25f
                holder.infoUserActivity.translationX=25f
            }
        }

    }

    class Holder : EpoxyHolder(){
        lateinit var epoxyview : EpoxyRecyclerView
        lateinit var categogy: TextView
        lateinit var infoUserActivity: TextView

        override fun bindView(itemView: View) {
            epoxyview= itemView.findViewById(R.id.viewpagerepoxy)
            categogy=itemView.findViewById(R.id.Category)
            infoUserActivity=itemView.findViewById(R.id.info_user_activity)
        }
    }
}