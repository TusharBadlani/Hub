package cessini.technology.commonui.epoxy.homegrid.grid

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import cessini.technology.commonui.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily

@EpoxyModelClass
abstract class GridModel : EpoxyModelWithHolder<GridModel.Holder>() {
    companion object {
        private const val TAG = "GridModel"
    }

    @EpoxyAttribute
    var type: Boolean= false
    @EpoxyAttribute
    lateinit var profession: String

    @EpoxyAttribute
    lateinit var height: Integer

    @EpoxyAttribute
    lateinit var width: Integer

    @EpoxyAttribute
    var name: String=""

    @EpoxyAttribute
    var imageUrl: String=""

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
    var color:Int =0
    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    val Float.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    override fun getDefaultLayout(): Int
    {
        if(type==true)
            return R.layout.item_single_room_grid
        else
            return R.layout.item_single_room_grid_for_listner
    }

    override fun bind(holder: Holder) {
        super.bind(holder)

        holder.profession.text = profession
        Log.d("Hemlo",width.toString()+"recieved")
        holder.cardView.layoutParams.height = height.toInt()
        holder.cardView.layoutParams.width = width.toInt()
        holder.cardView.setBackgroundColor(ContextCompat.getColor(holder.cardView.context,color))
        holder.name.text = name

        if (imageUrl == null || imageUrl.equals("")) {
            Glide.with(context).load(context.getDrawable(R.drawable.ic_user_defolt_avator))
                .into(holder.imgPerson)
        } else {
            Glide.with(context).load(imageUrl).into(holder.imgPerson)
        }
        val params:FrameLayout.LayoutParams =
            FrameLayout.LayoutParams(70.dp, 70.dp)
        params.gravity = Gravity.CENTER
        holder.imgPerson.layoutParams=params

        Log.d(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
        Log.d(TAG, "TOP Left: $topLeft")
        Log.d(TAG, "TOP RIGHT: $topRight")
        Log.d(TAG, "BOTTOM LEFT: $bottomLeft")
        Log.d(TAG, "BOTTOM RIGHT: $bottomRight")
        Log.d(TAG,"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")

        holder.cardView.shapeAppearanceModel =
            holder.cardView.shapeAppearanceModel.toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
                .setTopRightCorner(CornerFamily.ROUNDED, topRight)
                .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
                .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
                .build()

        holder.imgPerson.shapeAppearanceModel =
            holder.imgPerson.shapeAppearanceModel.toBuilder()
                .setTopLeftCorner(CornerFamily.ROUNDED, topLeft)
                .setTopRightCorner(CornerFamily.ROUNDED, topRight)
                .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeft)
                .setBottomRightCorner(CornerFamily.ROUNDED, bottomRight)
                .build()
    }


    class Holder() : EpoxyHolder() {
        lateinit var imgPerson: ShapeableImageView
        lateinit var name: TextView
        lateinit var profession: TextView
        lateinit var cardView: MaterialCardView

        override fun bindView(itemView: View) {
            cardView = itemView.findViewById(R.id.cardView)
            imgPerson = itemView.findViewById(R.id.imgPerson)
            name = itemView.findViewById(R.id.name)
            profession = itemView.findViewById(R.id.profession)
        }
    }
}



