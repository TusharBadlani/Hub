package cessini.technology.explore.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cessini.technology.explore.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide

@EpoxyModelClass
abstract class InfoModel : EpoxyModelWithHolder<InfoModel.Holder>(){

    @EpoxyAttribute
    lateinit var img :String
    @EpoxyAttribute
    lateinit var title1 :String
    @EpoxyAttribute
    lateinit var description1:String
    @EpoxyAttribute
    lateinit var view1:String

    override fun getDefaultLayout(): Int {
        return R.layout.info_view_item
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
            Glide.with(this.image.context).load(img).into(this.image)
            this.title.text= title1
            this.description.text= description1
            this.view.text= "${view1} views"
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var image : ImageView
        lateinit var title: TextView
        lateinit var description: TextView
        lateinit var view : TextView
        override fun bindView(itemView: View) {
            image= itemView.findViewById(R.id.img1)
            title= itemView.findViewById(R.id.title1)
            description= itemView.findViewById(R.id.user1)
            view= itemView.findViewById(R.id.category1)
        }
    }
}