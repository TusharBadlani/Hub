package cessini.technology.notifications.epoxydatabinding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


/**Setting up the ImageView.*/
@BindingAdapter("setImageView")
fun setImageView(view: ImageView, url: String) {
    Glide.with(view.context).load(url).into(view)
}

/**Setting up the Circular ImageView.*/
@BindingAdapter("setCircularImageView", requireAll = true)
fun setCircularImageView(view: CircleImageView, url: String) {
    Glide
        .with(view.context)
        .load(url)
        .centerCrop()
        .into(view)
}