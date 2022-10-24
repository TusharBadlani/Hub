package cessini.technology.camera.presentation.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cessini.technology.camera.R
import cessini.technology.camera.databinding.DialogeForCameraFragmentBinding
import cessini.technology.camera.databinding.FragmentVideoUploadBinding
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.presentation.globalviewmodels.BaseViewModel
import cessini.technology.commonui.presentation.globalviewmodels.GalleryViewModel
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.homemodels.StoryModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.story.StoryRepository
import cessini.technology.newrepository.video.VideoRepository
import com.otaliastudios.transcoder.Transcoder
import com.otaliastudios.transcoder.TranscoderListener
import com.otaliastudios.transcoder.sink.DataSink
import com.otaliastudios.transcoder.sink.DefaultDataSink
import com.otaliastudios.transcoder.source.DataSource
import com.otaliastudios.transcoder.source.FilePathDataSource
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.log10
import kotlin.math.pow


@AndroidEntryPoint
class VideoUploadFragment :
    BaseFragment<FragmentVideoUploadBinding>(R.layout.fragment_video_upload), TranscoderListener {

    companion object {
        private const val TAG = "VideoUploadFragment"
    }

    private var video: VideoModel? = null
    private var story: StoryModel? = null

    private val baseViewModel: BaseViewModel by activityViewModels()
    private val viewModel: GalleryViewModel by activityViewModels()

    @Inject
    lateinit var videoRepository: VideoRepository

    @Inject
    lateinit var storyRepository: StoryRepository

    private lateinit var dialogBinding: DialogeForCameraFragmentBinding
    private var startTime: Long = 0
    private lateinit var newCompressedFile: File
    private val compressed: String = "Compressed_"
    private lateinit var originalFile: File

    //BitMap for the Video Thumbnail
    private var bitmap: Bitmap? = null
    private lateinit var container: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        /**Setting the container for the viewGroup.*/
        if (container != null) {
            this.container = container
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** making view ready for story upload
         * by hiding some elements
         */

        binding.learnMore.setOnClickListener {
            val uri = Uri.parse("https://joinmyworld.in/faq.html")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }

        binding.userSwitch.setOnClickListener {
            binding.userSwitch.isChecked = false
            Toast.makeText(
                requireContext(),
                "This feature need 100 watch hours to active",
                Toast.LENGTH_SHORT
            ).show()
        }

        /**Setting up the lifeCycleOwner as this fragment.*/
        binding.lifecycleOwner = viewLifecycleOwner
        /**Setting up the viewModel Class in the ViewModel.*/
        binding.viewmodel = viewModel

        binding.videoUploadButton.setOnClickListener {
            it.isEnabled = false
            binding.videoUploadButton.setBackgroundResource(R.drawable.round_viewbutton)
            binding.videoUploadButton.setText("")
            binding.btnProgress.isVisible = true
            viewModel.insertNewVideo(baseViewModel.authFlag.value ?: false)
        }
    }


    override fun onResume() {
        super.onResume()
        setSpinner()

        Log.d(TAG, "Type: ${viewModel.uploadType.value}")
        if (viewModel.uploadType.value != 1) {
            binding.etVideoDescription.visibility = View.INVISIBLE
            binding.userVideoSelector.visibility = View.INVISIBLE
            binding.videoUploadButton.text = "Upload Viewer"
            binding.etVideoTitle.hint = "Viewer Caption"
        }

        video = viewModel.video.value
        story = viewModel.story.value
        /**Setting up the video Thumbnail of the video.*/
        if (video != null) {
            binding.userVideoThumbnail.setImageBitmap(video?.videoThumbnail)
        }

        if (story != null) {
            binding.userVideoThumbnail.setImageBitmap(story?.thumbnail)
        }

        binding.etVideoTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.videoUploadButton.setBackgroundResource(R.drawable.round_enable_viewbutton)
                binding.videoUploadButton.setTextColor(resources.getColor(R.color.white))
                if (binding.etVideoTitle.text?.toString().isNullOrBlank()) {
                    binding.videoUploadButton.setBackgroundResource(R.drawable.round_viewbutton)
                    binding.videoUploadButton.setTextColor(resources.getColor(R.color.cpHelpText))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        /** Action performed when user clicks on upload Button.
         * Uploading the file to the server and then sending back the user to the camera fragment. */
        viewModel.uploadProgress.observe(this, Observer {
            when (it) {
                100 -> {
                    binding.progressBarVideoUpload.visibility = View.GONE
//                    Toast.makeText(activity, "Upload Successful", Toast.LENGTH_SHORT).show()
//                    (activity as HomeActivity).showUploadSuccessfulDialog()
                    viewModel.setUploadProgress(-4)
                    viewModel.setVideoTitle("")
                    uploadVideoSuccessDialog()
                    Log.d(TAG, "Going back to CameraFrag")
//                    findNavController().navigateUp()
                }
                0 -> {
                    /**Hide the keyboard.*/
                    val inputMethodManager =
                        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

                    // Here preventing the video uploading action and directy navigate to camera fragment
//                    (activity as HomeActivity).showUploadSuccessfulDialog()
//                    findNavController().navigateUp()
                    /*binding.containerUploadSection.visibility = View.INVISIBLE
                    binding.progressBarVideoUpload.visibility = View.VISIBLE*/
                }
                -1 -> {
                    Toast.makeText(activity, "Upload Failed", Toast.LENGTH_SHORT).show()
                    binding.progressBarVideoUpload.visibility = View.GONE
                    binding.containerUploadSection.visibility = View.VISIBLE
                    viewModel.setUploadProgress(-4)
                }
                -2 -> {
                    if (viewModel.uploadType.value == 1) {
                        if (binding.etVideoTitle.text?.toString().isNullOrBlank()) {
                            binding.etVideoTitle.error = "Title is required!"
                            return@Observer
                        }

                        if (binding.userVideoSelector.selectedItem.toString() == "Select Videos Categories") {
                            setSpinnerError(
                                binding.userVideoSelector,
                                "Please Select any one field to proceed"
                            )
                            return@Observer
                        }

                        viewModel.setVideoCategory(binding.userVideoSelector.selectedItem.toString())
                        viewModel.insertVideoInAPI(requireContext())
//                        viewModel.insertVideoInDB()
                    } else {
//                        if (binding.userVideoSelector.selectedItem.toString() != "Select Videos Categories") {
                        viewModel.setVideoCategory("1")
//                                binding.userVideoSelector.selectedItem.toString()
                        viewModel.insertStoryInAPI(requireContext())
//                        }
//                        else {
//                            setSpinnerError(
//                                binding.userVideoSelector,
//                                "Please Select any one field to proceed"
//                            )
//                        }
                    }
                }

                -3 -> {
                    goToAuth()
                    viewModel.setUploadProgress(-4)
                }
            }
        })

        binding.backUploadIcon.setOnClickListener {
            cancelDialog()
        }
    }

    private fun goToAuth() {
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.AuthFlow
        )
    }

    /**Function to setup the Spinner for the Videos Categories.*/
    private fun setSpinner() {
        val list = arrayOf(
            "Select Videos Categories",
            "Film and animation",
            "Cars and vehicles",
            "Music",
            "Pets and animals",
            "Sport",
            "Travel and events",
            "Gaming",
            "People and blogs",
            "Comedy",
            "Entertainment",
            "News and politics",
            "How-to and style",
            "Education",
            "Science and technology",
            "Non-profits and activism"
        )
        val arrayAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            requireContext(),
            R.layout.spinner_list,
            list
        )
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        binding.userVideoSelector.adapter = arrayAdapter
    }

    private fun setSpinnerError(spinner: Spinner, error: String) {
        val selectedView = spinner.selectedView
        if (selectedView != null && selectedView is TextView) {
            spinner.requestFocus()
            selectedView.error = "error" // any name of the error will do
            selectedView.setTextColor(Color.RED) //text color in which you want your error message to be displayed
            selectedView.text = error // actual error message
            spinner.performClick() // to open the spinner list if error is found.
        }
    }

    private fun uploadVideoSuccessDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())

        val inflater = this.layoutInflater
        val dialogView: View =
            inflater.inflate(cessini.technology.commonui.R.layout.upload_video_alert_dialog, null)
        dialogView.findViewById<TextView>(R.id.textView9).text =
            resources.getString(
                R.string.msg_successful_vid,
                if (viewModel.uploadType.value == 1) "video" else "story"
            )
        dialogBuilder.setView(dialogView)
//        dialogBuilder.setCancelable(true)


        val btnOkay = dialogView.findViewById<TextView>(cessini.technology.commonui.R.id.button)

        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        // Creating Dynamic
        val displayRectangle = Rect()

        val window: Window = requireActivity().window
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle)
        alertDialog.window!!.setLayout(
            (displayRectangle.width() * 0.8f).toInt(),
            alertDialog.window!!.attributes.height
        )




        btnOkay.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigateUp()
        }

    }


    /** Function to Show a Alert Dialog For the Confirmation of Navigating Back to the Camera Fragment without uploading video. */
    private fun cancelDialog() {
        /** Setting the Layout for the Dialog. */
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialoge_for_camera_fragment,
            this.container,
            false
        )

        /** Building an alert dialog. */
        val mAlertDialog = AlertDialog.Builder(context).setView(dialogBinding.cardView2).show()
        mAlertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Creating Dynamic
        val displayRectangle = Rect()

        val mWindow: Window = requireActivity().window
        mWindow.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        mAlertDialog.window!!.setLayout(
            (displayRectangle.width() * 0.8f).toInt(),
            mAlertDialog.window!!.attributes.height
        )
        /** Dismiss the Alert Dialog as the user don't want to navigate back to camera fragment without uploading the video. */
        dialogBinding.cancelUpload.setOnClickListener {
            mAlertDialog.dismiss()
            findNavController().navigateUp()
        }

        /** Navigate back to camera fragment without uploading the video.*/
        dialogBinding.doUpload.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    /** Start the video compress */

    //todo : My Thought is We are not actually passig anything here
    private fun compressVideo() {
        startTime = SystemClock.uptimeMillis()

        /** Create the new compressed file*/
        newCompressedFile = File(
            this.context?.externalMediaDirs?.first(),
            compressed + "${System.currentTimeMillis()}.mp4"
        )

        /** Pass the new compressed video file path to save the data in it */
        val sink: DataSink = DefaultDataSink(newCompressedFile.absolutePath)
        val builder = Transcoder.into(sink)

        val path = viewModel.video.value!!.videoUrl

        val originalFile = File(path)

        /** Get the original recorded data source for video compression */
        val source: DataSource =
            FilePathDataSource(originalFile.absolutePath)
        builder.addDataSource(source)
        builder.setListener(this)
            .setVideoRotation(0)
            .setSpeed(1f)
            .transcode()

    }

    override fun onTranscodeProgress(progress: Double) {

    }

    override fun onTranscodeCompleted(successCode: Int) {
        if (successCode == Transcoder.SUCCESS_TRANSCODED) {
            /**Generating the thumbnail for the saved Video. */
            bitmap = viewModel.setUpThumbnail(newCompressedFile)
            Log.d(TAG, "Thumbnail bitmap created: $bitmap")

            /** Set the new compressed data to video model */
            video = if (bitmap != null) {
                VideoModel(bitmap!!, "", "", newCompressedFile.absolutePath, "")
            } else {
                bitmap?.let { VideoModel(it, "", "", newCompressedFile.absolutePath, "") }

            }
            Log.i("Video :", video.toString())
            binding.userVideoThumbnail.setImageBitmap(bitmap)

            Log.i("Original File name:", originalFile.name)
            Log.i("Original File size:", getFileSize(originalFile.length()))
            Log.i(
                "Compression took:",
                "${SystemClock.uptimeMillis() - startTime}ms"
            )
            Log.i("Compressed File name:", newCompressedFile.name)
            Log.i("Compressed File size:", getFileSize(newCompressedFile.length()))
            Log.i("Compressed File path:", newCompressedFile.absolutePath)

            /** Update the new video model value to view model*/
            viewModel.setVideo(video)
        }
    }

    override fun onTranscodeCanceled() {

    }

    override fun onTranscodeFailed(exception: Throwable) {

    }

    /** Return the file size of the Video. */
    private fun getFileSize(size: Long): String {
        if (size <= 0)
            return "0"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }


    override fun onStop() {
        super.onStop()
        viewModel.clearVideo()

    }
}


