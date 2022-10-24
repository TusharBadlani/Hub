package cessini.technology.commonui.presentation.uiutils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cessini.technology.commonui.R
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("setCircularImageView", requireAll = true)
fun setCircularImageView(view: CircleImageView?, url: String?) {
    if (view != null) {
        Glide
            .with(view.context)
            .load(url)
            .error(R.drawable.ic_user_defolt_avator)
            .centerCrop()
            .into(view)
    }
}

@BindingAdapter("load")
fun ImageView.setImage(url:Int)
{
    this.setImageResource(url)
}
@BindingAdapter("load1")
fun ShapeableImageView.setImage1(url:Int)
{
    this.setImageResource(url)
}
@BindingAdapter("height")
fun View.setDim1(dim:Int)
{
    this.layoutParams.height= dim
}
@BindingAdapter("width")
fun View.setDim2(dim:Int)
{
    this.layoutParams.width= dim
}