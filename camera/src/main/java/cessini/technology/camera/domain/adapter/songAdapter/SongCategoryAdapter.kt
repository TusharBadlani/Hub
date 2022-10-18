package cessini.technology.camera.domain.adapter.songAdapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.camera.databinding.SongCategoryRowBinding
import cessini.technology.camera.utils.diffutil.SongCategoryAdapterDiffUtilCallback
import cessini.technology.model.Audio
import cessini.technology.model.SongInfo

class SongCategoryAdapter(
    categoryList: ArrayList<Audio>,
    val context: Context,
    var musicListener: SongAdapter.MusicListener

) : RecyclerView.Adapter<SongCategoryAdapter.SongCategoryViewHolder>() {

    var categoryList: ArrayList<Audio> = arrayListOf()

    init {
        this.categoryList = categoryList
    }

    var viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool()

    class SongCategoryViewHolder(val binding: SongCategoryRowBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(categoryList: Audio) {
            /**Setting up the Categories Name.*/
            binding.songCategoryName = categoryList.category

            /**Setting up the Expandable View Text.*/
            binding.songCategoryExpandText = "View More"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongCategoryViewHolder {
        val view = SongCategoryRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SongCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongCategoryViewHolder, position: Int) {
        val currItem = categoryList[position]

        holder.bind(currItem)

        /**Setting up the Layout Manager For the Song Adapter.*/
        val layoutManager = LinearLayoutManager(
            holder.binding.rvMusicRecyclerView.context,
            LinearLayoutManager.VERTICAL,
            false
        )

        /**Initializing the song Adapter.*/
        val songAdapter: SongAdapter

        /**Populate 5 Audios By Default in the Category.*/
        val defaultListOfAudios: ArrayList<SongInfo> = ArrayList()

        Log.d("CategoryPositionAudio ", defaultListOfAudios.size.toString())
        for ((c, item) in currItem.uploads.withIndex()) {
            if (c + 1 == 5)
                break
            defaultListOfAudios.add(item)
        }

        /**By default the categories will be in non-expanded state and we're passing an default audio list to the song controller.*/
        songAdapter =
            SongAdapter(
                defaultListOfAudios,
                context,
                musicListener,
                holder.layoutPosition,
                this,
                holder.oldPosition
            )


        holder.binding.rvMusicRecyclerView.layoutManager = layoutManager
        holder.binding.rvMusicRecyclerView.adapter = songAdapter
        holder.binding.rvMusicRecyclerView.setRecycledViewPool(viewPool)


        Log.d("CategoryPositionIndex ", position.toString())

        holder.binding.setSongCategoryExpandClick {
            if (holder.binding.txtViewMore.text == "View More") {
                holder.binding.songCategoryExpandText = "View Less"
//                songAdapter.myListSong = currItem.audio
//                songAdapter.notifyDataSetChanged()
                addMoreSongs(categoryList[position].uploads, songAdapter, 1)
                Log.d("Category All songs ", currItem.uploads.size.toString())

            } else {
                holder.binding.songCategoryExpandText = "View More"
//                songAdapter.myListSong = defaultListOfAudios
//                songAdapter.notifyDataSetChanged()
                addMoreSongs(categoryList[position].uploads, songAdapter, 2)
                Log.d("Category Default songs ", defaultListOfAudios.size.toString())

            }
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    //Set data function using diffutil
    fun setData(newData: ArrayList<Audio>?) {
        val diffResult =
            DiffUtil.calculateDiff(SongCategoryAdapterDiffUtilCallback(newData, categoryList))
        diffResult.dispatchUpdatesTo(this)
        categoryList.clear()
        categoryList.addAll(newData!!)
    }


    private fun addMoreSongs(songList: ArrayList<SongInfo>, songAdapter: SongAdapter, type: Int) {
        val songs: ArrayList<SongInfo> = ArrayList()

        if (type == 1) {
            for (song in songList) {
                songs.add(song.clone())
            }
        } else {
            for ((c, _) in songList.withIndex()) {
                if (c + 1 == 5)
                    break
                songs.add(songList[c].clone())
            }
        }

        songAdapter.setData(songs)
    }

}