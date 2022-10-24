package cessini.technology.commonui.epoxy.homegrid.grid

import android.content.Context
import cessini.technology.commonui.R
import cessini.technology.newapi.model.CreatorListeners
import com.airbnb.epoxy.AsyncEpoxyController


class GridController2(
    private val screenHeight: Int,
    private val screenWidth: Int,
    private val context: Context
) : AsyncEpoxyController() {

    private val CORNER_RADIUS_MAX: Float = 90f
    var creatorAndListeners: List<CreatorListeners> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }




    override fun buildModels() {
        for (i in 0..creatorAndListeners.size-1) {

            grid {
                id(i)
                type(creatorAndListeners[i].isCreator)
                name(creatorAndListeners[i].name?:"atishay")
                profession("Some random profession")
                context(context)
                imageUrl(creatorAndListeners[i].profile_picture?:"")


                var itemHeight = getHeight(creatorAndListeners.size, i, screenHeight)
                var itemWidth = getWidth(creatorAndListeners.size, i, screenWidth)
                if (creatorAndListeners.size == 1 || creatorAndListeners.size == 2) {
                    spanSizeOverride { _, _, _ ->  2}
                } else {
                    if(creatorAndListeners.size%2!=0)
                    if (i == 0) {
                        spanSizeOverride { _, _, _ -> 2 }
                    } else {
                        spanSizeOverride { _, _, _ -> 1 }
                    }
                }
                height(itemHeight)
                width(itemWidth)
               color(colors[i])
//                topLeft(topLeftRightCornerRadius(creatorAndListeners.size, i))
//                topRight(topLeftRightCornerRadius(creatorAndListeners.size, i))
//                bottomLeft(bottomLeftCorner(creatorAndListeners.size, i))
//                bottomRight(bottomRightCorner(creatorAndListeners.size, i))
                topLeft(CORNER_RADIUS_MAX)
                topRight(CORNER_RADIUS_MAX)
                bottomLeft(CORNER_RADIUS_MAX)
                bottomRight(CORNER_RADIUS_MAX)


            }
        }
    }

    private fun getHeight(totalItem: Int, position: Int, screenHeight: Int): Int {
        val n = (totalItem + 1) / 2
        return when (totalItem) {
            1 -> {
                screenHeight
            }
            2 -> {
                screenHeight / 2
            }
            3, 4 -> {
                screenHeight / 2
            }
            else -> {
                if (totalItem % 2 == 0) {
                    screenHeight / (n / 2)
                } else {
                    screenHeight / ((n + 1) / 2)
                }
            }
        }
    }



    private fun getWidth(size: Int, position: Int, screenWidth: Int): Int {
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

    private fun topLeftRightCornerRadius(totalItem: Int, index: Int): Float {
        var radius = 0f
        if (index == 0) {
            radius = CORNER_RADIUS_MAX
        }
        return radius;
    }

    private fun bottomLeftCorner(totalItem: Int, index: Int): Float {
        var radius = 0f;
        when (totalItem) {
            1 -> {
                radius = CORNER_RADIUS_MAX
            }
            2 -> {
                if (index == 1) {
                    radius = CORNER_RADIUS_MAX
                }
            }
            3 -> {
                if (index == 1) {
                    radius = CORNER_RADIUS_MAX
                }
            }
            4 -> {
                if (index == 3) {
                    radius = CORNER_RADIUS_MAX
                }
            }
            5 -> {
                if (index == 3) {
                    radius = CORNER_RADIUS_MAX
                }
            }
        }
        return radius;

    }

    private fun bottomRightCorner(totalItem: Int, index: Int): Float {
        var radius = 0f;
        if (totalItem - 1 == index) {
            radius = CORNER_RADIUS_MAX
        }
        return radius;
    }
    val colors = arrayListOf<Int>(
        R.color.c1,R.color.c2,
        R.color.c3,R.color.c4

    )


}