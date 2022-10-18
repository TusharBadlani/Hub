package cessini.technology.camera.domain.adapter.songAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.camera.R
import cessini.technology.camera.databinding.MusicRecyclerRowBinding
import cessini.technology.camera.utils.Constant
import cessini.technology.camera.utils.diffutil.SongAdapterDiffUtilCallback
import cessini.technology.model.SongInfo


class SongAdapter(
    myListSong: ArrayList<SongInfo>,
    val context: Context,
    var musicListener: MusicListener,
    private var categoryPosition: Int,
    var songCategoryAdapter: SongCategoryAdapter,
    var oldPosition: Int
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    var myListSong: ArrayList<SongInfo> = arrayListOf()

    init {
        this.myListSong = myListSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /** Setting up the view for the recycler view and then returning the view. */
        val view = MusicRecyclerRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myListSong.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d("Song : ", myListSong[position].toString())

        val song = myListSong[position]

        /**Binding the Data of the Current Song.*/
        holder.bind(song)

        /**Perform Actions according to the Click in the view. */
        holder.onClick(song, musicListener, holder, categoryPosition, this, songCategoryAdapter)
    }

    //Set data function using diffutil
    fun setData(newData: ArrayList<SongInfo>?) {
        val diffResult = DiffUtil.calculateDiff(SongAdapterDiffUtilCallback(newData, myListSong))
        diffResult.dispatchUpdatesTo(this)
        myListSong.clear()
        myListSong.addAll(newData!!)
    }


    class ViewHolder(val binding: MusicRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

        /**Binding the data of the song into the view.*/
        fun bind(song: SongInfo) {
            /**Setting the view of the Play/Pause Button.*/
            if (Constant.isPlaying == song.id) {
                binding.musicPlayButton.setBackgroundResource(R.drawable.ic_pause_button)
            } else {
                binding.musicPlayButton.setBackgroundResource(R.drawable.ic_play_button)
            }

            /**Binding the Audio Title.*/
            binding.audioName = song.title!!
//                song.title.toString().substring(0, (song.title.toString().length - 4))

            /**Binding the Audio Thumbnail.*/
            if (!song.thumbnail.isNullOrEmpty()) {
                binding.setThumbnail = song.thumbnail.toString()
            } else {
                binding.musicThumbnail.setImageResource(R.drawable.ic_music)
            }

            /**Binding the Audio Duration.*/
            binding.songDuration = song.duration.toString()

            /**Setting the view for the Audio Select Button.*/
            if (Constant.selectSong != song.id) {
                binding.musicSelectButton1.visibility = View.GONE
            } else {
                binding.musicSelectButton1.visibility = View.VISIBLE
            }
        }

        /**Perform actions based on the clicks in the view. */
        fun onClick(
            song: SongInfo,
            musicListener: MusicListener,
            holder: ViewHolder,
            categoryPosition: Int,
            songAdapter: SongAdapter,
            songCategoryAdapter: SongCategoryAdapter
        ) {
            /** When User Click on Play Button . */

            binding.setOnPlayClicked { _ ->
                /**If the Music is Playing , then reset the buttons to PLay button and stop the media Player.*/
                if (Constant.isPlaying == song.id) {
                    holder.binding.musicPlayButton.setBackgroundResource(R.drawable.ic_play_button)
                    musicListener.onPause(song, binding)
                }
                /**If the Music is Not Playing , then reset the buttons to Pause button and start the media Player.*/
                else {
                    holder.binding.musicPlayButton.setBackgroundResource(R.drawable.ic_pause_button)
                    musicListener.onPlay(song, binding, categoryPosition)
                }
            }

            /**When User selects an audio.*/
            binding.setOnAudioSelected {
                /**If the Selected Song is button is visible , then set its visibility to GONE.
                 * Also set the (Constant.selectSong) variable as -1 to indicate its not visible*/
                if (Constant.selectSong == song.id) {
                    holder.binding.musicSelectButton1.visibility = View.GONE
                    Constant.selectSong = ""
                }
                /**If the Selected Song is button is visible , then set its visibility to VISIBLE.
                 * Also set the (Constant.selectSong) variable as songID to indicate its visibility.*/
                else {
                    holder.binding.musicSelectButton1.visibility = View.VISIBLE
                    Constant.selectSong = song.id
                }
                /**Notify the Adapter for the View Update by calling notifyDataSetChanged().*/
                songAdapter.notifyDataSetChanged()

                if (Constant.oldParentPos == -1) {
                    Constant.oldParentPos = categoryPosition
                } else if (Constant.oldParentPos != -1 && Constant.oldParentPos != categoryPosition) {
                    Log.i("Audio", "Old pos: ${Constant.oldParentPos}")
                    songCategoryAdapter.notifyItemChanged(Constant.oldParentPos)
                    Constant.oldParentPos = categoryPosition
                }
            }
            /**Sends the Selected Audio for Playback.*/
            binding.musicSelectButton1.setOnClickListener {
                musicListener.onAudioFileSelected(song)
            }
        }
    }

    interface MusicListener {

        /** Function returns the Audio File URL that has been Clicked in the View.*/
        fun onPlay(song: SongInfo, holder: MusicRecyclerRowBinding, categoryPosition: Int): String {
            return song.upload_file.toString()
        }

        /** Function returns the Audio File Url that has been Clicked in the View. */
        fun onPause(song: SongInfo, holder: MusicRecyclerRowBinding): String {
            return song.upload_file.toString()
        }

        /** Returns the Song Object when an Audio file is selected in the view. */
        fun onAudioFileSelected(song: SongInfo): SongInfo {
            return song
        }
    }
}