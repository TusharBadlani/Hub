package cessini.technology.commonui.presentation.common

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import cessini.technology.commonui.R
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@BindingAdapter("dayTime")
fun TextView.bindDayTime(time: Long) {
    visibility = if (time == 0L) View.GONE else View.VISIBLE
    text = time.dayTime()
}

@BindingAdapter("dayNTime")
fun TextView.bindDayNTime(time: Long) {
    visibility = if (time == 0L) View.GONE else View.VISIBLE
    text = time.dayNTime()
}

@BindingAdapter("gone")
fun View.bindGone(gone: Boolean) {
    visibility = if (gone) View.GONE else View.VISIBLE
}

@BindingAdapter("spinAdapter")
fun Spinner.bindAdapter(adapter: ArrayAdapter<*>) {
    this.adapter = adapter
}

@BindingAdapter("spinSelection")
fun Spinner.bindSelection(selection: AdapterView.OnItemSelectedListener) {
    onItemSelectedListener = selection
}

@BindingAdapter("creatorImage")
fun ImageView.bindCreatorImage(image: String) {
    load(image.ifEmpty { return }) {
        transformations(CircleCropTransformation())
    }
}
@BindingAdapter("visible")
fun View.setVisiblility(status:Int)
{
    isVisible = status.equals(1)
}
@BindingAdapter("setcolor")
fun View.setBackgroundColor(live:Boolean)
{
    if(live)
        this.setBackgroundColor(ContextCompat.getColor(this.context,R.color.cpRed))
    else
        this.setBackgroundColor(ContextCompat.getColor(this.context,R.color.cpHelpText))
}
@BindingAdapter("settext")
fun TextView.setText(live:Boolean)
{
 if(live)
     this.text= "Live"
    else
        this.text="Upcoming"
}


@BindingAdapter("listenerImage")
fun ImageView.bindListenerImage(image: String) {
    load(image.ifEmpty { return }) {
        transformations(CircleCropTransformation())
        placeholder(R.drawable.ic_user_defolt_avator)
        error(R.drawable.ic_user_defolt_avator)
    }

    setPadding(0, 0, 0, 0)
}

@BindingAdapter("onTouched")
fun setViewTouchListener(view: View, listener: View.OnTouchListener) {

    if(view!=null) {
        with(view) { setOnTouchListener(listener) }
    }
}

@BindingAdapter("ripple")
fun rippleListener(view: View, fragment: Fragment) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        while (true) rippleAnimate(view)
    }
}

private suspend fun rippleAnimate(view: View) {
    view.animate().alpha(1f).duration = 1000L
    delay(1000)
    view.animate().alpha(0f).duration = 1000L
    delay(1000)
}

@BindingAdapter("pageTransformer")
fun setViewPagerTransformer(viewPager: ViewPager2,flag:Boolean){

    val pageMarginPx = viewPager.context.resources.getDimensionPixelOffset(R.dimen.pageMargin)
    val offsetPx = viewPager.context.resources.getDimensionPixelOffset(R.dimen.offset)
    viewPager.setPageTransformer { page, position ->
        val vp = page.parent.parent as ViewPager2
        val offset = position * -(2 * offsetPx + pageMarginPx)
        if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
            if (ViewCompat.getLayoutDirection(vp) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                page.translationX = -offset
            } else {
                page.translationX = offset
            }
        } else {
            page.translationY = offset
        }
    }
}

@BindingAdapter("offScreenPageLimit")
fun setViewPagerTransformer(view: ViewPager2,limit:Int){
    view.offscreenPageLimit = limit
}


@BindingAdapter("changeBackground")
fun changeBackground(view: Button, status: Boolean) {

    if (status) {
        view.setBackgroundResource(R.drawable.round_enable_viewbutton)
        view.setTextColor(Color.parseColor("#ffffff"))
    }else{
        view.setBackgroundResource(R.drawable.round_viewbutton)
        view.setTextColor(Color.parseColor("#0F1419"))
    }

}

@BindingAdapter("positionType")
fun decorateItemByPositionType(view: ShapeableImageView, type: Int) {
    val radius = view.context.resources.getDimension(R.dimen.dimen_15)
    when (type) {
        0 -> {
            with(view) {
                shapeAppearanceModel = view.shapeAppearanceModel
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                    .build()

            }
        }

        // for last items
        /*2->{
            with(view) {
                shapeAppearanceModel = view.shapeAppearanceModel
                    .toBuilder()
                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                    .build()
            }
        }*/

        //for middle items
        else -> {
            with(view) {
                shapeAppearanceModel = view.shapeAppearanceModel
                    .toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                    .setTopRightCorner(CornerFamily.ROUNDED, 0f)
                    .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                    .build()
            }
        }
    }
}


