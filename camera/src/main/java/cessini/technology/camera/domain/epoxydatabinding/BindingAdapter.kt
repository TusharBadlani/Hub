package cessini.technology.camera.domain.epoxydatabinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide


/**Setting up the ImageView.*/
@BindingAdapter("setImageView")
fun setImageView(view: ImageView?, url: String?) {
    if (view != null) {
        Glide.with(view.context).load(url).into(view)
    }
}