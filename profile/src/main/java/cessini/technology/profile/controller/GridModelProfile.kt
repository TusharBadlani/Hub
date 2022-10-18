package cessini.technology.profile.controller

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.marginLeft

import cessini.technology.profile.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

@EpoxyModelClass
abstract class GridModelProfile : EpoxyModelWithHolder<Tabish>() {
    @EpoxyAttribute
    var height: Int=130

    @EpoxyAttribute
    var width: Int=90

    @EpoxyAttribute
    lateinit var imageUrl:String
    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute
    var topLeft: Float = 0f

    @EpoxyAttribute
    var topRight: Float = 0f

    @EpoxyAttribute
    var bottomLeft: Float = 0f

    @EpoxyAttribute
    var bottomRight: Float = 0f
    @EpoxyAttribute
  lateinit  var onClickListener :View.OnClickListener
  @EpoxyAttribute
   var clickable:Boolean =true

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    val Float.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    private fun convertDpToPixel(dp: Float): Float {
        val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
        val px: Float = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    override fun getDefaultLayout(): Int
    {

        return R.layout.gridsampleprofile
    }

    override fun bind(holder: Tabish) {
        super.bind(holder)

        holder.cardView.layoutParams.height= height.dp
        holder.cardView.layoutParams.width= width.dp
        holder.cardView.setOnClickListener(onClickListener)
        holder.imgPerson.isClickable=clickable
        holder.imgPerson.setOnClickListener(onClickListener)
        if(imageUrl!="")
            Glide.with(context).load(imageUrl)
                .into(holder.imgPerson)

        holder.cardView.shapeAppearanceModel =
            holder.cardView.shapeAppearanceModel.toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, convertDpToPixel(topLeft))
                .setTopRightCorner(CornerFamily.ROUNDED, convertDpToPixel(topRight))
                .setBottomLeftCorner(CornerFamily.ROUNDED, convertDpToPixel(bottomLeft))
                .setBottomRightCorner(CornerFamily.ROUNDED, convertDpToPixel(bottomRight))
                .build()

        holder.imgPerson.shapeAppearanceModel =
            holder.imgPerson.shapeAppearanceModel.toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, convertDpToPixel(topLeft))
                .setTopRightCorner(CornerFamily.ROUNDED, convertDpToPixel(topRight))
                .setBottomLeftCorner(CornerFamily.ROUNDED, convertDpToPixel(bottomLeft))
                .setBottomRightCorner(CornerFamily.ROUNDED, convertDpToPixel(bottomRight))
                .build()
    }


}
class Tabish : EpoxyHolder() {
    lateinit var imgPerson: ShapeableImageView
    lateinit var cardView: MaterialCardView

    override fun bindView(itemView: View) {
        cardView = itemView.findViewById(R.id.cardViewm)
        imgPerson = itemView.findViewById(R.id.imgPersonm)


    }
}