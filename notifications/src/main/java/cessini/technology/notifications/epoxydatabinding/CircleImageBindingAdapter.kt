package cessini.technology.notifications.epoxydatabinding

import androidx.databinding.BindingAdapter
import cessini.technology.notifications.R
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("setImage")
fun image(view: CircleImageView, url: String) {
    Glide
        .with(view.context)
        .load(url)
        .error(R.drawable.ic_user_defolt_avator)
        .centerCrop()
        .into(view)
}