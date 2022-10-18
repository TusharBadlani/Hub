package cessini.technology.home.controller

import android.annotation.SuppressLint
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import cessini.technology.model.Video
import cessini.technology.home.videoSuggestionHomeFragmentRecyclerItem
import cessini.technology.home.viewmodel.SocketFeedViewModel
import com.airbnb.epoxy.AsyncEpoxyController


class VideoSuggestionController(
    private val socketViewModel: SocketFeedViewModel
) : AsyncEpoxyController() {
    companion object {
        private const val TAG = "VideoSuggestionControl"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun buildModels() {
        allVideos.forEachIndexed { index, homeFeedResponseItem ->

            videoSuggestionHomeFragmentRecyclerItem {
                id(index)
                setVideoSuggestionThumbnail(homeFeedResponseItem.thumbnail)
                Log.d(TAG, "Model : $homeFeedResponseItem")


                var duration = ""
                val minutes = homeFeedResponseItem.duration!! / 60
                val seconds = homeFeedResponseItem.duration!! % 60
                if (minutes < 10) {
                    duration = duration + "0" + minutes.toString()
                } else {
                    duration += minutes.toString()
                }

                duration += ":"

                if (seconds < 10) {
                    duration = duration + "0" + seconds.toString()
                } else {
                    duration += seconds.toString()
                }

                setVideoSuggestionDuration(duration.substring(1, duration.length))

                setVideoSuggestionTitle(homeFeedResponseItem.title)

                var isClickTime = false
                val touchTimeFactor = 200
                var touchDownTime = 0L
                onTouchDetected { view, event ->

                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val lp = ConstraintLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        lp.setMargins(8,8,8,8)
                        view.layoutParams = lp
                        Log.i(TAG, "Pressed")
                        touchDownTime = System.currentTimeMillis()

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        val lp = ConstraintLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                        lp.setMargins(0,0,0,0)
                        view.layoutParams = lp
                        Log.i(TAG, "Released")
                        isClickTime = System.currentTimeMillis() - touchDownTime < touchTimeFactor
                    }
                    false
                }

                onVideoSuggestionClicked { _, _, _, position ->
                    if (isClickTime) {
                        socketViewModel.suggestedVideoClicked(position)
                        Log.i("VideoSugController", "$position")
                    }
                }
            }
        }
    }

    /**Setting up the VideosList for the suggestions.*/
    var allVideos: ArrayList<Video> =
        ArrayList()
        set(value) {
            field = value
            requestModelBuild()
        }
}
