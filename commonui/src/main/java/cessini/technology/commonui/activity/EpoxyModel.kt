package cessini.technology.commonui.activity

import android.content.Context
import android.view.View
import android.widget.TextView
import cessini.technology.commonui.R
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import org.kurento.client.RecorderEndpoint
import org.kurento.client.WebRtcEndpoint
import org.webrtc.EglBase
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoFileRenderer
import org.webrtc.VideoTrack

@EpoxyModelClass
abstract class EpoxyModel : EpoxyModelWithHolder<EpoxyModel.Holder>() {
    @EpoxyAttribute
    var creator: Boolean= false
    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    lateinit var context:EglBase.Context
    @EpoxyAttribute
     lateinit var track:VideoTrack

    @EpoxyAttribute
    lateinit var height1: Integer

    @EpoxyAttribute
    lateinit var width1: Integer

    @EpoxyAttribute
    lateinit var name1: String

    @EpoxyAttribute
     var imageId: Int =-1

    @EpoxyAttribute
    lateinit var context1: Context

    @EpoxyAttribute
    var tL: Float = 0f

    @EpoxyAttribute
    var tR: Float = 0f

    @EpoxyAttribute
    var bL: Float = 0f

    @EpoxyAttribute
    var bR: Float = 0f


    override fun getDefaultLayout(): Int
    {
        if(creator==true)
            return R.layout.card_layout_host
        else
            return R.layout.card_layout
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
            if (imageId != -1) {
                holder.imgPerson.setMirror(true)
                try {
                    holder.imgPerson.init(context, null)
                    holder.imgPerson.setEnableHardwareScaler(true)
                    holder.imgPerson.setZOrderMediaOverlay(true)
                    track.addSink(holder.imgPerson)
                } catch (e: Exception) {
                }

                holder.cardView.layoutParams.height = height1.toInt()
                holder.cardView.layoutParams.width = width1.toInt()
                holder.name.text = name1

                holder.cardView.shapeAppearanceModel =
                    holder.cardView.shapeAppearanceModel.toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, tL)
                        .setTopRightCorner(CornerFamily.ROUNDED, tR)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, bL)
                        .setBottomRightCorner(CornerFamily.ROUNDED, bR)
                        .build()

//        holder.imgPerson.shapeAppearanceModel =
//            holder.imgPerson.shapeAppearanceModel.toBuilder()
//                .setTopLeftCorner(CornerFamily.ROUNDED, tL)
//                .setTopRightCorner(CornerFamily.ROUNDED, tR)
//                .setBottomLeftCorner(CornerFamily.ROUNDED, bL)
//                .setBottomRightCorner(CornerFamily.ROUNDED, bR)
//                .build()
            }

    }

    class Holder() : EpoxyHolder() {
        lateinit var imgPerson: SurfaceViewRenderer
        lateinit var name: TextView
//        lateinit var profession: SurfaceViewRenderer
        lateinit var cardView: MaterialCardView

        override fun bindView(itemView: View) {
            cardView = itemView.findViewById(R.id.cardView)
            imgPerson = itemView.findViewById(R.id.localView)
            name = itemView.findViewById(R.id.name)
//          profession = itemView.findViewById(R.id.rView)
        }
    }
}



