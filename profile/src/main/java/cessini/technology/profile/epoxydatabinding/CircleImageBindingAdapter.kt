package cessini.technology.profile.epoxydatabinding

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import cessini.technology.profile.R
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("setImage", requireAll = true)
fun image(view: CircleImageView, url: String?) {
    Glide
        .with(view.context)
        .load(url)
        .error(R.drawable.ic_user_defolt_avator)
        .centerCrop()
        .into(view)
}