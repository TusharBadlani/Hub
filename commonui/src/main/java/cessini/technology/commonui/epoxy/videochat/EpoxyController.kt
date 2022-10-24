package cessini.technology.commonui.epoxy.videochat

import android.content.Context

import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.commonui.data.models.data
import com.airbnb.epoxy.AsyncEpoxyController


class EpoxyController(
    val screenHeight: Int,
    val screenWidth: Int,
    private val context: Context
) : AsyncEpoxyController() {
    var images: MutableList<data> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }
    private val CORNER_RADIUS_MAX: Float = 0f
    override fun buildModels() {
        var i = 0;
        val size = images.size
        images.forEachIndexed lit@ { index, it ->
            /**
             * setting the dimension according to the size of the array list
             */
            val h1 = getHeight(screenHeight, size, i)
            val h2 = getWidth(screenWidth, size, i)
            val tl = topleft(size, i)
            val tr = topRight(size, i)
            val bl = bottomleft(size, i)
            val br = bottomright(size, i)
      epoxy{
          id(i)
          creator(it.creater)
          name1(it.title)
          track(it.video)
          imageId(it.index)
          title("some random topic")
          height1(h1)
          width1(h2)
          tL(tl)
          tR(tr)
          bL(bl)
          bR(br)
          context1(context)
          context(it.con)
      }
            i++
        }
    }

    override fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        if (images.size == 1 || images.size % 2 == 0)
            return super.getSpanSizeLookup()

        val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (position) {
                    0 -> return 2
                    else -> return 1
                }
            }
        }
        return spanSizeLookup
    }


    private fun getHeight(screenHeight: Int, size: Int, position: Int): Int {
      when(size)
      {
          1->
              return screenHeight
          2,3,4 ->
              return screenHeight/2
          else ->
          {
              val rows= (size+1)/2;
              return screenHeight/(rows)
          }
      }
    }

    private fun getWidth(screenWidth: Int, size: Int, position: Int): Int {
        if (size <= 2)
            return screenWidth
        else if (size%2!=0) {
            if (position == 0)
                return screenWidth;
            else
                return screenWidth / 2
        } else {
            return screenWidth / 2
        }
    }

    private fun topleft(size: Int, pos: Int): Float {
        if (pos == 0)
            return CORNER_RADIUS_MAX
        return 0F
    }

    private fun topRight(size: Int, pos: Int): Float {
        when (size) {
            1 ->
                return CORNER_RADIUS_MAX
            2, 3 -> {
                if (pos == 0)
                    return CORNER_RADIUS_MAX
                else
                    return 0F
            }
            4 -> {
                if (pos == 1)
                    return CORNER_RADIUS_MAX
                else
                    return 0F
            }
            else ->
                return 0F
        }
    }

    private fun bottomleft(size: Int, pos: Int): Float {
        when (size) {
            1 ->
                return CORNER_RADIUS_MAX
            2, 3 -> {
                if (pos == 1)
                    return CORNER_RADIUS_MAX
                else
                    return 0F
            }
            4 -> {
                if (pos == 2)
                    return CORNER_RADIUS_MAX
                else
                    return 0F
            }
            else ->
                return 0F
        }
    }

    private fun bottomright(size: Int, pos: Int): Float {
        if (pos == size - 1)
            return CORNER_RADIUS_MAX
        return 0F
    }
}

