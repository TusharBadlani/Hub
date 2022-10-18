package cessini.technology.commonui.activity

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView


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