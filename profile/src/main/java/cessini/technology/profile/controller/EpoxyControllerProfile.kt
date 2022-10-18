package cessini.technology.profile.controller

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
//import cessini.technology.explore.fragment.ExploreFragmentDirections
import cessini.technology.model.RoomUsers
import com.airbnb.epoxy.AsyncEpoxyController
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel


class EpoxyControllerProfile(
    val screenHeight: Int,
    val screenWeight: Int,
    val category:String,
    val fragment: Fragment,
    var clickable:Boolean
) : AsyncEpoxyController() {
    var roomUsers: MutableList<RoomUsers> = mutableListOf()
        set(value) {
            field = value
            requestModelBuild()
        }

    private var CORNER_RADIUS_MAX: Float=5f
    override fun buildModels() {
        Log.d("Hemlo",roomUsers.toString())
        if(category=="")
            CORNER_RADIUS_MAX=10f
        var i = 0
        val size = roomUsers.size
        Log.e("ProfileRoomGrid","my size is $size")
        roomUsers.forEachIndexed { index, it ->
            val h1 = getHeight(screenHeight, size, i)
            val h2 = getWidth(screenWeight, size, i)
            val tl = topleft(size, i)
            val tr = topRight(size, i)
            val bl = bottomleft(size, i)
            val br = bottomright(size, i)
            gridModelProfile {
                id(index)
                imageUrl(it.url)
                height(h1)
                width(h2)
                topLeft(tl)
                context(fragment.context)
                topRight(tr)
                bottomRight(br)
                bottomLeft(bl)
                clickable(clickable)
             onClickListener{
                 _, ->
//                 fragment.findNavController().navigate(
//                     ExploreFragmentDirections.actionExploreFragmentToLiveFragment(
//                         "Suggestion Rooms",
//                         category
//                     )
//                 )
             }
            }
            i++
        }
    }


    override fun getSpanSizeLookup(): GridLayoutManager.SpanSizeLookup {
        if (roomUsers.size == 1 || roomUsers.size % 2 == 0)
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
        if (size == 1)
            return screenHeight
        else
            return screenHeight / 2;

    }

    private fun getWidth(screenWidth: Int, size: Int, position: Int): Int {
        if (size <= 2)
            return screenWidth
        else if (size == 3) {
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

