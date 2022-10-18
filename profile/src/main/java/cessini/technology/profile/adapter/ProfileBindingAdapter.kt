package cessini.technology.profile.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import cessini.technology.profile.R
import com.bumptech.glide.Glide

@BindingAdapter("imageUrl")
fun image(view: ImageView, url: String?) {
    Glide
        .with(view.context)
        .load(url)
        .placeholder(R.drawable.ic_user_defolt_avator)
        .circleCrop()
        .into(view)
}
