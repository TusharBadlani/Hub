package cessini.technology.commonui.presentation.storydisplay.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.databinding.FragmentCommonStoryDisplayItemBinding
import cessini.technology.cvo.homemodels.StoriesFetchModel
import cessini.technology.cvo.profileModels.StoryProfileModel
import com.bumptech.glide.Glide
import kohii.v1.core.Playback
import kohii.v1.core.controller
import kohii.v1.exoplayer.Kohii

class CommonStoryDisplayAdapter(
    private val listener: Listener,
    private val exoProvider: Kohii
) :
    RecyclerView.Adapter<CommonStoryDisplayAdapter.StoryViewHolder>() {

    companion object {
        private val TAG = CommonStoryDisplayAdapter::class.java.simpleName
    }

    private lateinit var context: Context
    private var storyList = emptyList<StoriesFetchModel>()

    fun submitItems(storyList: ArrayList<StoriesFetchModel>) {
        this.storyList = storyList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        context = parent.context
        val binding = FragmentCommonStoryDisplayItemBinding.inflate(
            LayoutInflater.from(context),
            parent, false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount() = storyList.size

    inner class StoryViewHolder(
        private val binding: FragmentCommonStoryDisplayItemBinding
    ) : RecyclerView.ViewHolder(binding.root), Playback.StateListener {

        fun bind(story: StoriesFetchModel) {

            binding.txtCaptionStory.text = story.caption
            binding.imgUserProfilePicStory.setOnClickListener {
                listener.onImgOrTxtUsernameClicked(story)
            }
            binding.txtUserNameStory.setOnClickListener {
                listener.onImgOrTxtUsernameClicked(story)
            }

            val currProfile = listener.getCurrentProfile(story)
            binding.txtUserNameStory.text = currProfile?.name
            Glide
                .with(context)
                .load(currProfile?.profilePicture)
                .placeholder(R.drawable.ic_user_defolt_avator)
                .error(R.drawable.ic_user_defolt_avator)
                .circleCrop()
                .into(binding.imgUserProfilePicStory)

            if (story.upload_file.isNullOrEmpty()) return
            exoProvider.setUp(story.upload_file!!) {
                tag = story.upload_file!!
                preload = true
                controller = controller { playback, _ ->
                    binding.storyItem.setOnClickListener {
                        with(playback.playable) {
                            this ?: return@setOnClickListener
                            binding.exoPlayerStory.player?.playWhenReady = !isPlaying()
                        }
                        playback.addStateListener(this@StoryViewHolder)
                    }
                }
            }.bind(binding.exoPlayerStory)

        }

        override fun onRendered(playback: Playback) {
            binding.storyProgressBar.visibility = View.GONE
        }

        override fun onPlaying(playback: Playback) {
            binding.storyProgressBar.visibility = View.GONE
        }

        override fun onBuffering(
            playback: Playback,
            playWhenReady: Boolean
        ) {
            binding.storyProgressBar.visibility = View.VISIBLE
        }

    }

    interface Listener {
        fun onImgOrTxtUsernameClicked(story: StoriesFetchModel)
        fun getCurrentProfile(story: StoriesFetchModel): StoryProfileModel?
    }

}
