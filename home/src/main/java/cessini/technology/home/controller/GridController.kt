//package cessini.technology.home.controller
//
//import android.content.Context
//import cessini.technology.commonui.epoxy.story.ChildRecyclerController
//import cessini.technology.commonui.HomeGrid.GridController
//import cessini.technology.home.EpoxyModelClasses.grid
//import cessini.technology.home.EpoxyModelClasses.gridModelRecycler
//import cessini.technology.model.Viewer
//import cessini.technology.newapi.model.CreatorListeners
//import com.airbnb.epoxy.AsyncEpoxyController
//import com.example.experiment.epoxy.storyModelRecycler
//
//class GridController(
//    private val screenHeight: Int,
//    private val screenWidth: Int,
//    private val context: Context
//) : AsyncEpoxyController() {
//
//    private val CORNER_RADIUS_MAX: Float = 70f
//    var creatorAndListeners: List<CreatorListeners> = emptyList()
//        set(value) {
//            field = value
//            requestModelBuild()
//        }
//
//    var viewerItems: List<Viewer> = emptyList()
//
//        set(value) {
//            field = value
//            requestModelBuild()
//        }
//
//
//    override fun buildModels() {
//        storyModelRecycler {
//            id("story_items")
//            val childController= ChildRecyclerController(context)
//            childController.viewerItems = viewerItems
//            spanSizeOverride { _, _, _ -> 2 }
//            controller(childController)
//        }
//       gridModelRecycler {
//           id("grid_item")
//           val gridController= GridController(screenHeight,screenWidth,context)
//           gridController.creatorAndListeners= creatorAndListeners
//          size(creatorAndListeners.size)
//           controller(gridController)
//       }
//
//        //val gridController = GridController(screenHeight, screenWidth, context)
//       // gridController.creatorAndListeners = creatorAndListeners
////                for (i in 0..creatorAndListeners.size-1) {
////            grid {
////                id(i)
////                name(creatorAndListeners[i].name)
////                profession("Some random profession")
////                context(context)
////                imageUrl(creatorAndListeners[i].profile_picture)
////
////
////                var itemHeight = getHeight(creatorAndListeners.size, i, screenHeight)
////                var itemWidth = getWidth(creatorAndListeners.size, i, screenWidth)
////
////                height(itemHeight)
////                width(itemWidth)
////
////                topLeft(topLeftRightCornerRadius(creatorAndListeners.size, i))
////                topRight(topLeftRightCornerRadius(creatorAndListeners.size, i))
////                bottomLeft(bottomLeftCorner(creatorAndListeners.size, i))
////                bottomRight(bottomRightCorner(creatorAndListeners.size, i))
////
////                if (creatorAndListeners.size == 1 || creatorAndListeners.size == 2) {
////                    spanSizeOverride { _, _, _ -> 2 }
////                } else {
////                    if (i == 0) {
////                        spanSizeOverride { _, _, _ -> 2 }
////                    } else {
////                        spanSizeOverride { _, _, _ -> 1 }
////                    }
////                }
////            }
////        }
////    }
////
////    private fun getHeight(totalItem: Int, position: Int, screenHeight: Int): Int {
////        val n = (totalItem + 1) / 2
////        return when (totalItem) {
////            1 -> {
////                screenHeight
////            }
////            2 -> {
////                screenHeight / 2
////            }
////            3, 4 -> {
////                screenHeight / 2
////            }
////            else -> {
////                if (totalItem % 2 == 0) {
////                    screenHeight / (n / 2)
////                } else {
////                    screenHeight / ((n + 1) / 2)
////                }
////            }
////        }
////    }
////
////
////
////    private fun getWidth(totalItem: Int, position: Int, screenWidth: Int): Int {
////        return when (totalItem) {
////            1 -> {
////                screenWidth
////            }
////            2 -> {
////                screenWidth
////            }
////            else -> {
////                if (position == 0) {
////                    screenWidth
////                } else {
////                    screenWidth / 2
////                }
////            }
////        }
////    }
////
////    private fun topLeftRightCornerRadius(totalItem: Int, index: Int): Float {
////        var radius = 0f
////        if (index == 0) {
////            radius = CORNER_RADIUS_MAX
////        }
////        return radius;
////    }
////
////    private fun bottomLeftCorner(totalItem: Int, index: Int): Float {
////        var radius = 0f;
////        when (totalItem) {
////            1 -> {
////                radius = CORNER_RADIUS_MAX
////            }
////            2 -> {
////                if (index == 1) {
////                    radius = CORNER_RADIUS_MAX
////                }
////            }
////            3 -> {
////                if (index == 1) {
////                    radius = CORNER_RADIUS_MAX
////                }
////            }
////            4 -> {
////                if (index == 3) {
////                    radius = CORNER_RADIUS_MAX
////                }
////            }
////            5 -> {
////                if (index == 3) {
////                    radius = CORNER_RADIUS_MAX
////                }
////            }
////        }
////        return radius;
////
////    }
////
////    private fun bottomRightCorner(totalItem: Int, index: Int): Float {
////        var radius = 0f;
////        if (totalItem - 1 == index) {
////            radius = CORNER_RADIUS_MAX
////        }
////        return radius;
//    }
//
//    }
