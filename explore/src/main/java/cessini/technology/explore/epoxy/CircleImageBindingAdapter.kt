package cessini.technology.explore.epoxy

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import cessini.technology.explore.R
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("setImage", requireAll = true)
fun image(view: CircleImageView, url: String?) {
    Glide
        .with(view.context)
        .load(url)
        .placeholder(R.drawable.ic_user_defolt_avator)
        .error(R.drawable.ic_user_defolt_avator)
        .centerCrop()
        .into(view)
}