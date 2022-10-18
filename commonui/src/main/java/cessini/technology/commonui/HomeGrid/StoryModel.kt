package cessini.technology.commonui.HomeGrid

import cessini.technology.commonui.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide

@EpoxyModelClass
abstract class StoryModel : EpoxyModelWithHolder<StoryModel.Holder>() {

//    @EpoxyAttribute
//    lateinit var drawable: Drawable

    @EpoxyAttribute
    lateinit var imageUrl: String

    @EpoxyAttribute
    lateinit var context:Context

    override fun bind(holder: Holder) {
//        Glide.with(context).load(drawable).into(holder.imgDrawable)
        Glide.with(context).load(imageUrl).into(holder.imgDrawable)
    }

    override fun getDefaultLayout(): Int {
        return R.layout.circular_video_suggestion_recycler_item
    }

    class Holder : EpoxyHolder() {
        lateinit var imgDrawable: ImageView
        override fun bindView(itemView: View) {
            imgDrawable = itemView.findViewById<ImageView>(R.id.circular_video_suggestion)
        }
    }
}
