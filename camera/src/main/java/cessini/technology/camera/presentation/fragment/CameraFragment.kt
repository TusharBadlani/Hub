package cessini.technology.camera.presentation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.media.session.PlaybackState.*
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import cessini.technology.camera.R
import cessini.technology.camera.databinding.FragmentCameraBinding
import cessini.technology.camera.domain.service.AudioService
import cessini.technology.camera.domain.viewmodel.CameraViewModel
import cessini.technology.camera.utils.Constant
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseFragment
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.viewmodel.basicViewModels.GalleryViewModel
import cessini.technology.cvo.cameraModels.CameraModes
import cessini.technology.cvo.cameraModels.VideoModel
import cessini.technology.cvo.homemodels.StoryModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.lang.Runnable
import java.util.*
import kotlin.math.floor
import kotlin.math.min


@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(R.layout.fragment_camera) {
    companion object {
        private const val TAG = "CameraFragment"
    }

    var isAudio = false

    val viewModel: GalleryViewModel by activityViewModels()
    private val cameraViewModel: CameraViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()

    var video: VideoModel? = null

    //BitMap for the Video Thumbnail
    private var bitmap: Bitmap? = null

    //CountDown Timer For Start Recording
    private var countDownTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long = 3
    private var timer: Timer? = null

    //Timing for recording
    var recordTimeVideo = 60
    var recordTimeLim = recordTimeVideo
    var recordTimeStory = 36
    var recordTimeSong = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        systemBarInsetsEnabled = false
        binding.cameraViewModel = cameraViewModel.also { it.cameraFragment = this }
        // me
//        requireActivity().window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                viewModel.isOpenFirstTime = true
            }
            false
        }

    }


    /** Setup the fragment when the Camera Fragment is in the window.*/
    private fun setUpFragmentViews() {
        if ((context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.CAMERA
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.RECORD_AUDIO
                )
            } != PackageManager.PERMISSION_GRANTED)
            && (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            }) != PackageManager.PERMISSION_GRANTED) {
            binding.cameraCapture.isEnabled = false
            binding.cameraCaptureButtonStart.isEnabled = false
            binding.cameraMusic.isEnabled = false
            binding.cameraCaptureButtonStop.isEnabled = false
            binding.gallerySelector.isEnabled = false
            binding.cameraWelcometext.visibility = View.VISIBLE
            binding.cameraExpresstext.visibility = View.VISIBLE
            binding.cameraButtanTakepermission.visibility = View.VISIBLE
            binding.cameraButtanTakepermission.setOnClickListener {
                Log.d(TAG, "Permission was not found.")
                askPermission()
                Log.d(TAG, "Permission was asked for the Camera Fragment")
            }
        } else {
            Log.d(TAG, "Permission is granted.")
            binding.cameraCaptureButtonStart.isEnabled = true
            binding.cameraCaptureButtonStop.isEnabled = true
            binding.cameraCapture.isEnabled = true
            binding.cameraMusic.isEnabled = true
            binding.gallerySelector.isEnabled = true
            binding.cameraSwitch.isEnabled = true
            binding.cameraWelcometext.visibility = View.GONE
            binding.cameraExpresstext.visibility = View.GONE
            binding.cameraButtanTakepermission.visibility = View.GONE
            setUpBottomCameraMode()
            Log.d(TAG, "Starting Camera")
            startCamera()
            if (viewModel.isOpenFirstTime) {
                val sheet = CameraOptionBottomSheetFragment()
                sheet.isCancelable = true
                sheet.show(childFragmentManager, sheet.tag)
            }
//            findNavController().navigate(R.id.action_cameraFragment_to_cameraOptionBottomSheetFragment)

            viewModel.loadVideoPathList(requireContext())
            val videoList = viewModel.pathListVideo.value

            if (videoList != null && videoList.isNotEmpty()) {
                val path = videoList[0]
                val videoFile = File(path)
                val bitmap = viewModel.setUpThumbnail(videoFile)

                binding.gallerySelector.setImageBitmap(bitmap)
            }
        }


        /**Start Recording*/
        binding.cameraCaptureButtonStart.setOnClickListener {
            Log.d(TAG, "Camera Recording Setup")
            binding.cameraMusic.visibility = View.GONE
            binding.gallerySelector.visibility = View.GONE
            binding.cameraSwitch.visibility = View.INVISIBLE
            binding.cameraModes.visibility = View.GONE
            setTimer()
            Log.d(TAG, "Recording timer established.")
            binding.cameraCaptureButtonStop.visibility = View.VISIBLE
            binding.cameraCaptureButtonStart.visibility = View.GONE
            binding.cameraTimer.visibility = View.GONE
        }

        /**Stop Recording*/
        binding.cameraCaptureButtonStop.setOnClickListener {
            countDownTimer?.cancel()
            Constant.isRecording = false
            stopRecording()
            Log.d(TAG, "Recording stopped.")
            binding.cameraCaptureButtonStart.visibility = View.VISIBLE
            binding.cameraCaptureButtonStop.visibility = View.GONE
        }

        /** Switch Camera Between Front and Back Camera */
        binding.cameraSwitch.setOnClickListener {
            stopServices()
            if (Constant.count == 500) {
                switchCamera()
                Constant.count = 2
            } else {
                Constant.count++
                switchCamera()
            }
        }

        /** Flash On and Off */
        binding.cameraFlash.setOnClickListener {
            if (!Constant.isFlash) {
                flashON()
                Constant.isFlash = true
            } else {
                flashOFF()
                Constant.isFlash = false
            }
        }

        /** Music Fetching and Selection */
        binding.cameraMusic.setOnClickListener { music ->
            stopServices()

            music.isEnabled = false
            music.isVisible = false

            lifecycleScope.launch {
                delay(500)
                /**Fetching the Audios and passing it to the music fragment.*/
                music.findNavController()
                    .navigate(R.id.action_cameraFragment_to_musicBottomSheetFragment)

                binding.cameraMusic.isEnabled = true
                binding.cameraMusic.isVisible = true

            }

        }
        /** Timmer */

        binding.cameraTimer.setOnClickListener {

            if (restTimerDuration == 5L) {
                restTimerDuration = 10L

            } else if (restTimerDuration == 10L) {
                restTimerDuration = 3L
            } else
                restTimerDuration = 5L
            Toast.makeText(
                requireContext(),
                "Timmer set now $restTimerDuration",
                Toast.LENGTH_SHORT
            ).show()

        }

        /** Gallery setup and Fetching the Files from the storage. */
        binding.gallerySelector.setOnClickListener {
            stopServices()
            /**using navigation component to enter Video Gallery BottomSheet Fragment*/
            it.findNavController().navigate(R.id.action_cameraFragment_to_videoGalleryFragment)
        }


        /** Getting back to Home Fragment */
        binding.cameraViewBack.setOnClickListener {
            if (Constant.isRecording) {
                stopRecording()
                Constant.isRecording = false
            }
            viewModel.isOpenFirstTime = true
            it.findNavController().navigateUp()
        }
    }

    /** Function to set the bottom modes of the camera .*/
    private fun setUpBottomCameraMode() {
        val listOFModes = ArrayList<CameraModes>()
        listOFModes.add(CameraModes("STORY"))
        listOFModes.add(CameraModes("VIDEO"))
        listOFModes.add(CameraModes("ROOM"))

        binding.changeCameraOption.setOnClickListener {

            val sheet = CameraOptionBottomSheetFragment()
            sheet.isCancelable = false
            sheet.show(childFragmentManager, sheet.tag)
//            it.findNavController().navigate(R.id.action_cameraFragment_to_cameraOptionBottomSheetFragment)
        }

    }

    /**Binding the Camera with Application's LifeCycle using Camera Provider.*/

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        //Creating a Listener . This let us know that weather our application has been binded with the camera.
        val cameraProvider = context?.let { ProcessCameraProvider.getInstance(it) }
        cameraProvider?.addListener(
            {
                val cameraProvider = cameraProvider.get()
                Constant.preview = Preview.Builder().build()

                //TODO(Memory leak: Holds a reference to ConstraintLayout)
                Constant.preview!!.setSurfaceProvider(binding.cameraView.surfaceProvider)

                Constant.recordVideo = VideoCapture.Builder().apply {
                    setTargetResolution(Size(480, 640))
                    setDefaultResolution(Size(480, 640))
                    setMaxResolution(Size(480, 640))
                }.build()


                //Camera Selector . By Default it will open back camera
                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()
                //Unbinding the CameraProvider
                cameraProvider.unbindAll()
                //Binding the camera

                //TODO (Memory Leak: camera holds an instance of fragment when fragment is destroyed)
                Constant.camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    Constant.preview,
                    Constant.recordVideo
                )

            }, ContextCompat.getMainExecutor(requireContext())
        )
    }

    /**Switch Front And Back Camera*/
    private fun switchCamera() {
        flashOFF()
        if (Constant.count % 2 == 0) {
            startCamera()
        } else {
            //Creating a Listener . This let us know that weather our application has been binded with the camera.
            val cameraProvider = context?.let { ProcessCameraProvider.getInstance(it) }
            cameraProvider?.addListener(Runnable {

                val cameraProvider = cameraProvider.get()
                Constant.preview = Preview.Builder().build()
                Constant.preview!!.setSurfaceProvider(binding.cameraView.surfaceProvider)

                Constant.recordVideo = VideoCapture.Builder().build()

                //Camera Selector . It will open the front camera
                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                //Unbinding the CameraProvider
                cameraProvider.unbindAll()
                //Binding the camera
                Constant.camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    Constant.preview,
                    Constant.recordVideo
                )

            }, ContextCompat.getMainExecutor(requireContext()))
        }
    }

    /**Timer counter */
    //val camera_timer = findViewById(R.id.image_view) as ImageView

    /**Start Recording*/
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun startRecording() {
        binding.cameraSwitch.isEnabled = false
        binding.gallerySelector.visibility = View.GONE

//        /** Getting the storage Directory for the file to be saved in the path. */
//        val storageDirectory: File? = File("${Environment.getExternalStorageDirectory()}/MyWorld/Videos")
//        storageDirectory?.mkdirs()

        Log.d(
            TAG,
            "Microphone Status : Is Microphone muted = ${
                cameraViewModel.setMicMuted(
                    (activity as HomeActivity),
                    false
                )
            }"
        )

        /**Creating the file in the Myworld directory.*/
//        val file : File = File(storageDirectory , "${System.currentTimeMillis()}.mp4")

        val file = File(
            this.context?.externalMediaDirs?.first(),
            "${System.currentTimeMillis()}.mp4"
        )
        Constant.recordVideo?.camera?.cameraControlInternal?.triggerAf()
        Constant.recordVideo?.startRecording(
            VideoCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(requireContext()),
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    Log.d(TAG, "$file Saved Successfully.")

                    /**Get the Duration of the File.*/
                    val duration = viewModel.getDuration(file)
                    Log.d(TAG, "Duration of ${file.name} is : $duration")

                    /**Generating the thumbnail for the saved Video. */
                    bitmap = setUpThumbnail(file)
                    Log.d(TAG, "Thumbnail bitmap created: $bitmap")



                    if (viewModel.uploadType.value == 1) {
                        /** for video **/
                        video = if (bitmap != null) {
                            VideoModel(bitmap!!, "", "", file.absolutePath, "")
                        } else {
                            bitmap?.let { VideoModel(it, "", "", file.absolutePath, "") }
                        }

                        Log.d(TAG, "VideoModel created: $video")

                        /** Sending the recorded video URI to the VideoUploadFragment.
                         * Fetching the recorded video from the storage using the URI and then uploading the Video to the server.
                         * Sending the to The Upload Video Fragment.*/
                        if (video != null) {
                            viewModel.setVideo(video)
                            Log.d(TAG, "ViewModel updated with ${viewModel.video.value}")

//                        Log.d("CameraFrag", "Calling MuxUtil Functions")
//                        viewModel.callMuxUtil()
                            findNavController().navigate(R.id.action_cameraFragment_to_videoUploadFragment)
                        }


                    } else if (viewModel.uploadType.value == 0) {
                        /** for story **/
                        if (bitmap != null) {
                            viewModel.setStory(
                                StoryModel(
                                    "",
                                    bitmap!!,
                                    duration?.toInt()!!,
                                    file.absolutePath
                                )
                            )
                        } else {
                            bitmap?.let {
                                viewModel.setStory(
                                    StoryModel(
                                        "",
                                        it,
                                        duration?.toInt()!!,
                                        file.absolutePath
                                    )
                                )
                            }
                        }

                        Log.d(TAG, "StoryModel created: ${viewModel.story.value}")

                        /** Sending the recorded video URI to the VideoUploadFragment.
                         * Fetching the recorded video from the storage using the URI and then uploading the Video to the server.
                         * Sending the User to The Upload Video Fragment.*/
                        if (viewModel.story.value != null) {
                            Log.d(TAG, "ViewModel updated with ${viewModel.story.value}")

//                        Log.d("CameraFrag", "Calling MuxUtil Functions")
//                        viewModel.callMuxUtil()
                            findNavController().navigate(R.id.action_cameraFragment_to_videoUploadFragment)
                        }
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Log.i(TAG, "Video Error: $message")
                }
            })

    }

    /**Stop Recording*/
    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        binding.progressBar.progress = 0
        //Chronometer Reset after stopping.
        countDownTimer?.cancel()
        timer?.cancel()
        binding.timer.visibility = View.GONE
        //Resetting the timer
        restProgress = 0
        restTimerDuration = 5

        //Stopping the Audio Service
        if (cameraViewModel.isService.value == true) {
            stopServices()
        }

        binding.recordingTimer.stop()
        binding.recordingTimer.visibility = View.GONE
//        binding.recordingTimerDot.visibility = View.GONE

        //Recording of the video has been stopped.
        Constant.recordVideo?.stopRecording()

        binding.cameraViewBack.visibility = View.VISIBLE
        binding.cameraCaptureButtonStart.visibility = View.VISIBLE
        binding.gallerySelector.visibility = View.VISIBLE
        binding.cameraCaptureButtonStop.visibility = View.GONE
        binding.cameraMusic.visibility = View.VISIBLE
        binding.cameraSwitch.visibility = View.VISIBLE
        binding.cameraSwitch.isEnabled = true
        binding.cameraModes.visibility = View.VISIBLE
        Log.d(TAG, "Video Recording stopped")

        Log.d(TAG, "Bitmap" + bitmap.toString())
        //user_video_thumbnail.setImageBitmap(bitmap)
    }

    /** Setup the Thumbnail of the recorded Video. */
    private fun setUpThumbnail(file: File): Bitmap? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ThumbnailUtils.createVideoThumbnail(file, Size(180, 200), CancellationSignal())
        } else {
            ThumbnailUtils.createVideoThumbnail(
                file.absolutePath,
                MediaStore.Video.Thumbnails.MINI_KIND
            )
        }
    }

    /**Set CountDown Timer For the Recording.
     *Here we have started a timer of 5 seconds so the 5000 is milliseconds is 5 seconds and the countdown interval is 1 second so it 1000.*/


    private fun setTimer() {
        binding.timer.visibility = View.VISIBLE
        countDownTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // It is increased to ascending order
                restProgress++
                // Current progress is set to text view in terms of seconds.
                binding.timer.text = (restTimerDuration.toInt() - restProgress).toString()
                Log.d(TAG, "Timer : ${(restTimerDuration.toInt() - restProgress)}")
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                binding.cameraViewBack.visibility = View.INVISIBLE
                binding.timer.visibility = View.INVISIBLE
                binding.recordingTimer.base = SystemClock.elapsedRealtime()
                binding.recordingTimer.visibility = View.VISIBLE
//                binding.recordingTimerDot.visibility = View.VISIBLE
                Constant.isRecording = true
                binding.cameraCaptureButtonStop.visibility = View.VISIBLE
                binding.cameraCaptureButtonStart.visibility = View.GONE
                if (!isAudio) {
                    Log.d(TAG, "Recording without Audio from Music List")
                    startRecording()
                } else {
                    Log.d(TAG, "Recording with Audio from Music List")
                    startService()
                    startRecording()
                }
                updateProgressBar()
                binding.cameraSwitch.visibility = View.INVISIBLE
                binding.recordingTimer.start()
                restProgress = 0
                restTimerDuration = 5
            }
        }.start()
    }

    /** Update the progress bar as the recording is going.
     * Stop the recording as the progress bar has completed its progress to 100 percent. */
    private fun updateProgressBar() {
        cameraViewModel.audio.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                recordTimeLim = min(it.duration, recordTimeLim)
            }
        })
        var counter = 0
        timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                counter++
                Log.d("COUNTER", counter.toString())
                binding.progressBar.progress = (counter * floor(100.00 / recordTimeLim)).toInt()

                if (100 - binding.progressBar.progress < floor(100.00 / recordTimeLim)) {
                    binding.progressBar.progress = 100
                }

                if (counter == recordTimeLim) {
                    timer!!.cancel()
                    MainScope().launch {
                        withContext(Dispatchers.Main) {
                            countDownTimer?.cancel()
                            Constant.isRecording = false
                            stopRecording()
                            binding.cameraCaptureButtonStart.visibility = View.VISIBLE
                            binding.cameraCaptureButtonStop.visibility = View.GONE
                        }
                    }
                }
            }
        }

        timer!!.schedule(timerTask, 0, 1000)
    }

    /** Switch On the Flash */
    private fun flashON() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Constant.camera?.cameraControl?.enableTorch(true)
            Constant.isFlash = true
        }
    }

    /** Switch OFF the Flash    */
    private fun flashOFF() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Constant.camera?.cameraControl?.enableTorch(false)
            Constant.isFlash = false
        }
    }

    /**     Ask For Permissions */
    private fun askPermission() {
        ActivityCompat.requestPermissions(
            activity as Activity, arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            Constant.PERMISSION_REQUEST_CODE
        )
    }

    /**     Check Permissions required for the Camera   */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.PERMISSION_REQUEST_CODE) {
            Log.i("Reach", "Reached + {${Constant.isPermission}}")
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Reach", "Reached + {${Constant.isPermission}}")
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Reach", "Reached + {${Constant.isPermission}}")
                    if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        Log.i("Reach", "Reached + {${Constant.isPermission}}")
                        if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                            Log.i("Reach", "Reached + {${Constant.isPermission}}")
                            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                                Log.i("Reach", "Reached + {${Constant.isPermission}}")
                                Constant.isPermission = true
                                binding.cameraCaptureButtonStop.isEnabled = true
                                binding.cameraSwitch.isEnabled = true
                                binding.cameraWelcometext.visibility = View.INVISIBLE
                                binding.cameraExpresstext.visibility = View.INVISIBLE
                                binding.cameraButtanTakepermission.visibility = View.INVISIBLE
                                binding.cameraCaptureButtonStart.isEnabled = true
                                binding.cameraMusic.isEnabled = true
                                binding.gallerySelector.isEnabled = true
                            }
                        }
                    }
                }
            }
        } else {
            Toast.makeText(
                context, "Permissions are required to run the application!\nKindly allow.",
                Toast.LENGTH_SHORT
            ).show()
            askPermission()
        }
    }

    /**Setup the Audio Name if Selected.*/
    private fun setUpAudioName() {
        /**Checking for the Song Name in the Music Bottom Sheet Fragment.
         * If the Audio Name is null then, set as "FOR YOU ".
         * Else , Set the Audio Name in the Field.
         */
        cameraViewModel.audio.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                binding.musicTitleCameraFragment.text =
                    it.title.toString().subSequence(0, (it.title.toString().length - 4))
            } else {
                binding.musicTitleCameraFragment.text = ""
            }
        })
    }

    /**Function to stop and reset all the services when the fragment is paused or stopped.*/
    private fun stopServices() {
        if (Constant.isRecording) {
            stopRecording()
        }
        restProgress = 0
        restTimerDuration = 5
        binding.progressBar.progress = 0
        val intent = Intent(context, AudioService()::class.java)
        Log.d(TAG, "Audio Service has Stopped.")
        intent.action = ACTION_PAUSE.toString()
        cameraViewModel.audio.value = null
        requireContext().startService(intent)
        cameraViewModel.isService.value = false
    }

    /**Function to prepare the services when the user select an audio for the recording.*/
    private fun prepareServices() {
        cameraViewModel.audio.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                val intent = Intent(context, AudioService()::class.java)
                intent.action = ACTION_PREPARE.toString()
                cameraViewModel.audio.value?.let {
                    Log.d(
                        TAG,
                        "SongURL for Audio Service :  ${it.upload_file}"
                    )
                }
                intent.putExtra("URL", it.upload_file.toString())
                Log.d(TAG, "Audio PREPARE Service has Started.")
                requireContext().startService(intent)
                cameraViewModel.isService.value = true
            }
        }
    }

    /**Function to play the prepared audio for the recording.*/
    private fun startService() {
        if (cameraViewModel.audio.value != null) {
            Log.d(TAG, "File Duration = ${cameraViewModel.audio.value!!.duration}")
            recordTimeVideo = cameraViewModel.audio.value!!.duration
        }
        val intent = Intent(context, AudioService()::class.java)
        intent.action = ACTION_PLAY.toString()
        Log.d(TAG, "Audio PLAY Service has Started.")
        requireContext().startService(intent)
        cameraViewModel.isService.value = true
    }

    private fun subscribeObservers() {
        viewModel.uploadProgress.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            when (it) {
                100 -> {
//                    (activity as HomeActivity).showUploadSuccessfulDialog()
                    Log.d(TAG, "Going back to CameraFrag")

                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        subscribeObservers()
        isAudio = false
        /** Function to setup the navigation and hide the status bar in Camera Fragment.*/

        /**Set the Audio Title selected by the user .*/
        setUpAudioName()

        setCameraOptionText()

        /** Setup the fragment when the Camera Fragment is in the window.*/
        setUpFragmentViews()

        cameraViewModel.loadAudio(requireContext())

        restProgress = 0
        restTimerDuration = 5
        binding.cameraCaptureButtonStart.isEnabled = true
        if (timer != null) {
            Log.d(TAG, "Progress Timer has been Canceled.")
            timer!!.cancel()
            timer = null
        }
        binding.progressBar.progress = 0
        if (Constant.isPermission) {
            binding.cameraCaptureButtonStop.isEnabled = true
            binding.cameraWelcometext.visibility = View.INVISIBLE
            binding.cameraExpresstext.visibility = View.INVISIBLE
            binding.cameraSwitch.isEnabled = true
            binding.cameraButtanTakepermission.visibility = View.INVISIBLE
            binding.cameraCaptureButtonStart.isEnabled = true
            binding.cameraMusic.isEnabled = true
            binding.gallerySelector.isEnabled = true
        }
        startCamera()

        if (Constant.isRecording) {
            Constant.isRecording = false
            stopRecording()
        }

        cameraViewModel.audio.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                isAudio = true
                if (cameraViewModel.isService.value == false) {
                    Log.d(TAG, "Preparing Service for the recording has been initiated.")
                    prepareServices()
                }
            }
        })
    }

    private fun setCameraOptionText() {
        viewModel.uploadType.observe({ lifecycle }) {
            when (it) {
                0 -> {
                    recordTimeLim = recordTimeStory
                    binding.changeCameraOption.text = "Viewer"
                }
                1 -> {
                    recordTimeLim = recordTimeVideo
                    binding.changeCameraOption.text = "Video"
                }
            }
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause called")
        stopServices()
        cameraViewModel.setMicMuted(
            (activity as HomeActivity),
            false
        )
        super.onPause()
    }

    override fun onStop() {
        /** To ReStore the View to its Original state .*/
        Log.d(TAG, "onStop called")
        stopServices()


        cameraViewModel.setMicMuted(
            (activity as HomeActivity),
            false
        )

        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }

        if (Constant.isRecording) {
            binding.recordingTimer.stop()
//            binding.recordingTimerDot.visibility = View.GONE
            stopRecording()
        }

        Constant.camera = null
        Constant.preview = null
    }
}
