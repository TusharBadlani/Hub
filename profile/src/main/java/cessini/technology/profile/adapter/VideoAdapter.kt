package cessini.technology.profile.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.databinding.CommonVideoAdapterBinding
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.profile.utils.Constant
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory

class VideoAdapter(private var videoModel: ArrayList<VideoModel>) :
    RecyclerView.Adapter<VideoAdapter.MyViewHolder>() {

    companion object {
        private const val TAG: String = "VideoAdapter"
    }

    private lateinit var binding: CommonVideoAdapterBinding

    /**late init var player : PlayerView */
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    /**Initialise load control. */
    var loadControl: LoadControl = DefaultLoadControl()

    /**Initialise band width meter. */
    private val bandWidthMeter by lazy {
        DefaultBandwidthMeter()
    }

    /**Initialize track selector */
    private var trackSelector: TrackSelector =
        DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandWidthMeter))


    class MyViewHolder(val binding: CommonVideoAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** This Function binds all the data of the video with the current View to display the data of the video. */
        fun bind(videoModel: VideoModel) {
            binding.executePendingBindings()

            /** Binding the thumbnail of the video.*/
            if (videoModel.videoThumbnail != null) {
                Glide.with(binding.root.context).load(videoModel.videoThumbnail)
                    .into(binding.profileVideoUserImage)
            } else {
                Glide.with(binding.root.context).load(videoModel.videoUrl)
                    .into(binding.profileVideoUserImage)
            }
            /**Binding the video Description.*/
            binding.profileVideoDescription.text = videoModel.videoDesc
            /**Binding the video title.*/
            binding.profileVideoUserName.text = videoModel.videoTitle
        }

        /** This function works according to the click made on views . */
        fun onClick(holder: MyViewHolder) {
            /** To Play and Pause the Current Video playing in ExoPlayer on Long Press in the view. */
            binding.root.setOnClickListener {

                with(binding.playerView.player) {
                    this ?: return@with

                    binding.imgPlay.visibility = if (playWhenReady) View.GONE else View.VISIBLE
                    playWhenReady = !playWhenReady

                }
            }
        }
    }

    /** Create the View For the Current Item .
     * Also , Initialize the Gesture Detector.
     * Returns the Inflated View to the recycler View , For setting up the UI of current Item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        /** Binding the View with the ViewPager. */
        binding =
            CommonVideoAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        Log.d(TAG, videoModel.size.toString())
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.imgPlay.visibility = View.GONE

        /** Binding the data of the video with the UI. */
        holder.bind(videoModel[position])

        /** Actions according to the clickListener. */
        holder.onClick(
            holder
        )

        initializePlayer(videoModel[position], holder)
    }

    override fun getItemCount(): Int {
        return videoModel.size
    }

    /** Initialize Simple ExoPlayer Without DASH Streaming .*/
    private fun initializePlayer(
        video: VideoModel,
        holder: MyViewHolder
    ) {
        //Initialize Simple ExoPlayer
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
            holder.binding.playerView.context,
            trackSelector,
            loadControl
        )

        //Initialize data source factory
        val factory = DefaultHttpDataSourceFactory("exoplayer_video")

        //Initialize extractors factory
        val extractorsFactory: ExtractorsFactory = DefaultExtractorsFactory()

        //Initialize media source
        val mediaSource: MediaSource =
            ExtractorMediaSource(
                Uri.parse(video.videoUrl),
                factory,
                extractorsFactory,
                null,
                null
            )

        //Set Player
        holder.binding.playerView.player = simpleExoPlayer

        //Keep screen on
        holder.binding.playerView.keepScreenOn = true

        //Prepare media
        simpleExoPlayer.prepare(mediaSource)
        Log.d(TAG, mediaSource.toString())

        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_ONE

        //Play media when Player is ready
        simpleExoPlayer.playWhenReady = false
        Constant.currentVolumeOfExoPlayer = simpleExoPlayer.volume
    }

    /** Play the Video in the view when a new view is attached to the window. */
    override fun onViewAttachedToWindow(holder: MyViewHolder) {
        super.onViewAttachedToWindow(holder)

        /**View Attached to the Window.*/
        Log.d(TAG, "View Attached :  ${holder.layoutPosition}")

        /** Starting the exoplayer with the current video when the item is attached into the view. */
        holder.binding.playerView.player?.playWhenReady = true
    }

    /** Pause the Video On the View when scrolled. */
    override fun onViewDetachedFromWindow(holder: MyViewHolder) {
        super.onViewDetachedFromWindow(holder)

        /**View Detached from the Window.*/
        Log.d(TAG, "View Detached : ${holder.layoutPosition}")

        /** Stopping the exoplayer. */
        holder.binding.playerView.player?.playWhenReady = false
    }
}