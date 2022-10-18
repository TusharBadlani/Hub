package cessini.technology.home.epoxy

import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.bumptech.glide.Glide
import cessini.technology.home.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView


/**Setting up the ImageView.*/
@BindingAdapter("setImageView")
fun setImageView(view: ImageView?, url: String?) {
    if (view != null) {
        Glide.with(view.context).load(url)
            .error(R.drawable.ic_user_defolt_avator).centerCrop().into(view)
    }
}

/**Setting up the Circular ImageView.*/
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

/**Setting up the Controller Epoxy Recycler View.*/
@BindingAdapter("setController")
fun setController(view: EpoxyRecyclerView?, controller: AsyncEpoxyController?) {
    if (controller != null) {
        view?.setController(controller)
    }
}

@BindingAdapter("setAdapter")
fun setAdapter(view: EpoxyRecyclerView?, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
    view?.adapter = adapter
}

@BindingAdapter("setPager")
fun bindViewPagerTabs(view: TabLayout, pagerView: ViewPager2) {
    TabLayoutMediator(view, pagerView) { _, _ ->
    }.attach()

    //To auto scroll the slider viewpager
    val handler = Handler()

    pagerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            handler.removeMessages(0)

            val runnable = Runnable {
                if (position == pagerView.adapter?.itemCount!! - 1) {
                    pagerView.currentItem = 0
                } else {
                    pagerView.currentItem = ++pagerView.currentItem
                }

            }
            if (position < pagerView.adapter?.itemCount!!) {
                Log.i("Position", pagerView.currentItem.toString())
                Log.i("Adapter Count", pagerView.adapter?.itemCount.toString())

                handler.postDelayed(runnable, 3000)

            }
        }
    })

}