package cessini.technology.commonui.HomeGrid

import androidx.databinding.BindingAdapter
import cessini.technology.commonui.R
import com.bumptech.glide.Glide
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
class BindingAdapter {
}