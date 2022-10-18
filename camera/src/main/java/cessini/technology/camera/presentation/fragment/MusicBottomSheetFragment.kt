package cessini.technology.camera.presentation.fragment

import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.camera.R
import cessini.technology.camera.databinding.BottomSheetLayoutBinding
import cessini.technology.camera.databinding.MusicRecyclerRowBinding
import cessini.technology.camera.domain.adapter.songAdapter.SongAdapter
import cessini.technology.camera.domain.adapter.songAdapter.SongCategoryAdapter
import cessini.technology.camera.domain.viewmodel.CameraViewModel
import cessini.technology.camera.utils.Constant
import cessini.technology.camera.utils.Constant.Companion.mediaPlayer
import cessini.technology.model.Audio
import cessini.technology.model.SongInfo
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class MusicBottomSheetFragment :
    BottomSheetDialogFragment(), LifecycleObserver {

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CameraViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_layout, container, false)
        binding.musicBottomSheetViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        /**Fetching the list of Audio.*/
        getAudioList()

        Log.d("TESTING", "Profile Fragment Called.")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog).behavior.apply {
            state = STATE_EXPANDED
        }
        binding.setAudioTitle = "Musics"
    }

    /**Function to fetch all the Audios.*/
    private fun getAudioList() {
        viewModel.listOfAudios.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                loadSong()
                hideShimmer()

//                binding.recyclerView.adapter!!.notifyDataSetChanged()
                addMoreCategories(it, binding.recyclerView.adapter as SongCategoryAdapter)
            }
        })
    }

    override fun onStart() {

        /**Setting up the Controller for the epoxy Recycler View.*/
//        setController()
        getAudioList()

        super.onStart()
    }

    /** Load Audios into the view. */
    private fun loadSong() {
        /** Setting up the Music Recycler View for displaying the music list */
        binding.recyclerView.apply {
            //  Setting the layout manager
            layoutManager = LinearLayoutManager(context)
            /** Setting up the adapter of the recycler view. */
            adapter = SongCategoryAdapter(
                viewModel.listOfAudios.value!!,
                requireContext(),
                object : SongAdapter.MusicListener {

                    override fun onPlay(
                        song: SongInfo,
                        holder: MusicRecyclerRowBinding,
                        categoryPosition: Int
                    ): String {
                        Log.i("AdapterCategoryPosition", "" + categoryPosition)
                        if (Constant.isPlaying != "") {
                            mediaPlayer.reset()
                        }
                        //Log.i("SongUrl" , song.songURL.toString())
                        val myUri: Uri? = song.upload_file?.toUri() // initialize Uri here
                        mediaPlayer.apply {
                            setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .build()
                            )
                            if (myUri != null) {
                                try {
                                    /**Setting up the DataSource for the media player.*/
                                    setDataSource(requireContext(), myUri)
                                    prepareAsync()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                                this.setOnPreparedListener {
                                    Constant.isPlaying = song.id
                                    adapter?.notifyItemChanged(categoryPosition)
                                    start()
                                }
                            }
                            /**Setting back the icons back to original state after the music player has completed the playing.*/
                            mediaPlayer.setOnCompletionListener {
                                holder.musicPlayButton.setBackgroundResource(R.drawable.ic_play_button)
                            }
                        }
                        return super.onPlay(song, holder, categoryPosition)
                    }

                    override fun onPause(song: SongInfo, holder: MusicRecyclerRowBinding): String {
                        /**If the player is playing the audio,
                         * then reset the media player and then mark Constant.isPlaying as false
                         */
                        if (Constant.isPlaying == song.id) {
                            mediaPlayer.reset()
                        }
                        Constant.isPlaying = ""
                        return super.onPause(song, holder)
                    }

                    override fun onAudioFileSelected(audio: SongInfo): SongInfo {
                        Log.i("Song", audio.toString())
                        /** Setting the Name of the selected Audio file. */
                        Constant.SONG_NAME =
                            audio.title.toString().substring(0, (audio.title.toString().length - 4))
//                        binding.musicListTitleCameraFragment.text = Constant.SONG_NAME

                        /**Hiding the Music Bottom Sheet Fragment.*/
                        (dialog as? BottomSheetDialog)?.behavior?.apply {
                            state = STATE_HIDDEN
                        }
                        /** Setting the Music Title as Audio File Selected. */
                        if (Constant.SONG_NAME != null) {
                            Log.d("Song_Selected", Constant.SONG_NAME.toString())
                            viewModel.audio.value = audio
                        }
                        return super.onAudioFileSelected(audio)
                    }
                })
        }
    }

    override fun onResume() {

        /**Fetching the list of Audio.*/
        getAudioList()

        /** Resetting the MediaPlayer */
        mediaPlayer.reset()
        super.onResume()
    }

    override fun onPause() {
        Constant.isPlaying = ""
        mediaPlayer.reset()
        super.onPause()
    }

    override fun onDestroyView() {
        Constant.isPlaying = ""
        mediaPlayer.reset()
        super.onDestroyView()

        _binding = null
    }

    private fun addMoreCategories(
        categoryList: ArrayList<Audio>,
        songCategoryAdapter: SongCategoryAdapter
    ) {
        val categories: ArrayList<Audio> = ArrayList()


        for (category in categoryList) {
            categories.add(category.clone())
        }

        songCategoryAdapter.setData(categories)
    }

    /**Function to Stop the Shimmer effect.*/
    private fun hideShimmer() {
//        binding.musicShimmerEffect.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        /**Resetting the Song Adapter View.*/
        Constant.selectSong = ""
    }
}