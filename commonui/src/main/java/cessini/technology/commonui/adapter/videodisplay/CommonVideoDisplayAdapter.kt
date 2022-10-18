package cessini.technology.commonui.adapter.videodisplay

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.databinding.CommonVideoAdapterBinding
import cessini.technology.model.Video
import com.bumptech.glide.Glide
import kohii.v1.core.Common
import kohii.v1.core.Playback
import kohii.v1.core.controller
import kohii.v1.exoplayer.Kohii

class CommonVideoDisplayAdapter(
    private val listener: Listener,
    private val exoProvider: Kohii
) :
    RecyclerView.Adapter<CommonVideoDisplayAdapter.MyViewHolder>() {

    companion object {
        private val TAG: String = CommonVideoDisplayAdapter::class.java.simpleName
    }

    private var videos = emptyList<Video>()
    private lateinit var context: Context

    fun submitList(videos: List<Video>) {
        this.videos = videos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val binding = CommonVideoAdapterBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount() = videos.size

    inner class MyViewHolder(
        private val binding: CommonVideoAdapterBinding
    ) : RecyclerView.ViewHolder(binding.root), Playback.StateListener {

        fun bind(video: Video) {

            binding.profileVideoDescription.text = video.title
            binding.profileVideoUserName.text = video.profile.name
            binding.videoViewCount.text = video.viewCount.toString()

            Glide.with(context)
                .load(video.profile.picture)
                .circleCrop()
                .into(binding.profileVideoUserImage)

            binding.profileVideoUserName.setOnClickListener {
                listener.onProfileVideoUserNameOrImgClicked(video)
            }

            binding.profileVideoUserImage.setOnClickListener {
                listener.onProfileVideoUserNameOrImgClicked(video)
            }

            listener.getLikedStatus(video.id) { isLiked ->
                binding.userLike.setImageResource(
                    if (isLiked) R.drawable.ic_likeactive else R.drawable.ic_like_inactive
                )
            }

            binding.userLike.setOnClickListener {
                listener.onLikeButtonPressed(video.id)
                listener.getLikedStatus(video.id) { isLiked ->
                    binding.userLike.setImageResource(
                        if (isLiked) R.drawable.ic_likeactive else R.drawable.ic_like_inactive
                    )
                }

            }

            binding.userComment.setOnClickListener {
                listener.onCommentButtonPressed(video.id)
            }

            exoProvider.setUp(video.url) {
                tag = "${video.url}$bindingAdapterPosition"
                preload = true
                repeatMode = Common.REPEAT_MODE_ALL
                controller = controller { playback, _ ->
                    binding.root.setOnClickListener {
                        with(playback.playable) {
                            this ?: return@setOnClickListener
                            binding.playerView.player?.playWhenReady = !isPlaying()
                            if (!isPlaying()) binding.imgPlay.visibility = View.VISIBLE
                        }
                    }

                    playback.addStateListener(this@MyViewHolder)
                }
            }.bind(binding.playerView)

        }

        override fun onRendered(playback: Playback) {
            binding.imgPlay.visibility = View.GONE
            binding.profileVideoProgressBar.visibility = View.GONE
        }

        override fun onPlaying(playback: Playback) {
            binding.imgPlay.visibility = View.GONE
            binding.profileVideoProgressBar.visibility = View.GONE
        }

        override fun onBuffering(playback: Playback, playWhenReady: Boolean) {
            binding.profileVideoProgressBar.visibility = View.VISIBLE
        }

        override fun onPaused(playback: Playback) {
            listener.onPlaybackPaused(
                videos[bindingAdapterPosition].id,
                binding.playerView.player?.duration?.toInt() ?: 10
            )
        }

    }

    interface Listener {
        fun onProfileVideoUserNameOrImgClicked(video: Video)
        fun getLikedStatus(videoId: String, callback: (Boolean) -> Unit)
        fun onLikeButtonPressed(videoId: String)
        fun onCommentButtonPressed(videoId: String)
        fun onPlaybackPaused(videoId: String, duration: Int)
    }

}
