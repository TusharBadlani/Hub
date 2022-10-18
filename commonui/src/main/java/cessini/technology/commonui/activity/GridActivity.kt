package cessini.technology.commonui.activity

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.activity.live.PeerConnectionAdapter
import cessini.technology.commonui.activity.live.SdpAdapter
import cessini.technology.commonui.activity.live.SignalingClient
import cessini.technology.commonui.activity.services.screen_share.MediaProjectionService
import cessini.technology.commonui.databinding.CommonChatSnackviewBinding
import cessini.technology.commonui.fragment.commonChat.CommonChatFragment
import cessini.technology.commonui.viewmodel.commonChat.CommonChatPayload
import cessini.technology.commonui.viewmodel.commonChat.CommonChatViewModel
import cessini.technology.newapi.services.commonChat.CommonChatSocketHandler
import com.airbnb.epoxy.EpoxyRecyclerView
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.util.IOUtils
import com.google.android.gms.common.internal.service.Common
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.slf4j.helpers.Util.report
import org.webrtc.*
import org.webrtc.PeerConnectionFactory.InitializationOptions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class GridActivity : AppCompatActivity() , SignalingClient.Callback {




    private lateinit var snackViewBinding : CommonChatSnackviewBinding
    private lateinit var snackbar: Snackbar
    private lateinit var customSnackView: CommonChatSnackviewBinding

    private var isFront = true

    private val CAPTURE_PERMISSION_REQUEST_CODE = 1


    private val REQUEST_CODE_STREAM = 179 //random num

    private val REQUEST_CODE_RECORD = 180 //random num

    private var mMediaProjectionPermissionResultData: Intent? = null
    private var mMediaProjectionPermissionResultCode = 0

    val KEY = true

    var STREAM_NAME_PREFIX = "android_device_stream"
    var videoFileName=""
    var fileName: String? = "VID" + System.currentTimeMillis() + ".mp4".also {
        videoFileName = it
    }

    private val awsconfig="""
  {
  "UserAgent": "MobileHub/1.0",
  "Version": "1.0",
  "CredentialsProvider": {
"CognitoIdentity": {
  "Default": {
    "PoolId": "ap-south-1:1ef82df2-73d2-4714-a978-5afbf42dc816",
    "Region": "ap-south-1"
  }
}
  },
  "IdentityManager": {
    "Default": {}
  },
  "S3TransferUtility": {
    "Default": {
      "Bucket": "kurento-recorded-file",
      "Region": "ap-south-1"
    }
  }
}"""
    private val amplifyconfig="""
  {
    "auth": {
        "plugins": {
            "awsCognitoAuthPlugin": {
                "IdentityManager": {
                    "Default": {}
                },
                "CredentialsProvider": {
                    "CognitoIdentity": {
                        "Default": {
                            "PoolId": "ap-south-1:1ef82df2-73d2-4714-a978-5afbf42dc816",
                            "Region": "ap-south-1"
                        }
                    }
                },
                "CognitoUserPool": {
                    "Default": {
                        "PoolId": "ap-south-1_tdZpslecO",
                        "AppClientId": "64frfm3gdkcnhij3amj9750dq7",
                        "Region": "ap-south-1"
                    }
                }
                
            }
        }
    },
    "storage": {
        "plugins": {
            "awsS3StoragePlugin": {
                  "bucket": "kurento-recorded-file",
                  "region": "ap-south-1"
            }
        }
    }
}"""

    private val audioManager by lazy { RTCAudioManager.create(this) }
    private lateinit var fileRenderer:FileRenderer
    private lateinit var remoteVideoTrack:VideoTrack
    private lateinit var eglBaseContext: EglBase.Context
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var localView: SurfaceViewRenderer
    private lateinit var mediaStream: MediaStream
    private lateinit var iceServers: List<PeerConnection.IceServer>
    private lateinit var peerConnectionMap: HashMap<String, PeerConnection>
    private lateinit var  remoteViews: Array<SurfaceViewRenderer>
    private lateinit var camera: ImageView
    private lateinit var screenShare: Button
    private lateinit var audio: ImageView

    private  var isScreenShare=false
    private  var isCameraShare=false
   private lateinit var recAdapter:RecAdapter
    private var count = 0


    /** Storing data model of local user camera and screen to handle it in epoxy */
    private var userData : data? = null
    private var userDataScreen : data? = null

    private var endPointUrlBase: String = "rtmp://Ant-M-RTMPL-1G6LL9TH0KN27-3cbc7d0e3e5e05ad.elb.ap-south-1.amazonaws.com"

    val options = InitializationOptions.builder(this)
        .createInitializationOptions()
    var remoteViewsIndex = 1
    private var videoTrack:VideoTrack? = null
    private var videoTrackScreen:VideoTrack? = null
    private var localAudioTrack : AudioTrack? = null
    private var localAudioTrackScreen : AudioTrack? = null
    private val audioSource by lazy { peerConnectionFactory.createAudioSource(MediaConstraints())}
    private val chatViewModel : CommonChatViewModel by viewModels()
    var rname="RoomLive"

    val recyclerDataArrayList:ArrayList<data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grid)

        setupCustomSnackBar()




        (this@GridActivity).window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            navigationBarColor = ContextCompat.getColor(this@GridActivity,R.color.Dark)

        }

        // Streaming video
        val mediaProjectionService: MediaProjectionService? = MediaProjectionService.INSTANCE
        if (mediaProjectionService != null) {

            val sendIntent = mediaProjectionService.sendIntent()
            if (sendIntent != null) {
                startActivityForResult(sendIntent, REQUEST_CODE_STREAM)
            }
        }


        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // Get Permission for screen Capturing
        if(mediaProjectionManager!=null){
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE_STREAM)
        }

        Log.e("jsom",JSONObject(awsconfig).toString())
        /**
         * AWS Config
         */
        AWSMobileClient.getInstance().initialize(applicationContext,AWSConfiguration(JSONObject(awsconfig)),object : Callback<UserStateDetails?> {
            override fun onResult(result: UserStateDetails?) {
                Log.e("aws",result.toString())
            }

            override fun onError(e: java.lang.Exception?) {
                Log.e("aws error", "onError: ", e)
            }
        })

        val endcall=findViewById<Button>(R.id.btnSchedule)
        audio=findViewById<ImageView>(R.id.iv_audio)
        val ivMessage = findViewById<ImageView>(R.id.ivMessage)
        val share = findViewById<ImageView>(R.id.share)
        val cameraSwitch = findViewById<ImageView>(R.id.cameraSwitch)
        camera = findViewById<ImageView>(R.id.iv_video)
        screenShare = findViewById<Button>(R.id.btnShareScreen)
//        localView = findViewById(R.id.localView)
        handleIntent(intent)
        if(rname=="RoomLive") {
            try {
                rname = intent.getStringExtra("Room Name").toString()
                Log.e("room name", rname)

            } catch (e: Exception) {

            }
        }

        chatViewModel.setSocket(rname)
        chatViewModel.listenTo(rname)
        chatViewModel.messages.observe(this){
            //TODO
            val lastMessage = it.last()
            showSnack(lastMessage)
        }





        cameraSwitch.setOnClickListener(){
            switchCamera(cameraSwitch)
        }

        audio.setOnClickListener() {
            switchAudio(audio)
            Log.e("closed", "connection Closed")
        }

        camera.setOnClickListener {
            switchVideo(camera)

        }


        share.setOnClickListener {
            createRoomLink()
        }
        endcall.setOnClickListener {
           onBackPressed()
        }

        screenShare.setOnClickListener {
            onScreenShare()
        }
        peerConnectionMap = HashMap()

        audioManager.selectAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)

        iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302")
                .createIceServer(),

            PeerConnection.IceServer.builder("stun:rooms-api.joinmyworld.live")
                .createIceServer(),

            PeerConnection.IceServer.builder("turn:rooms-api.joinmyworld.live")
                .setUsername("Admin")
                .setPassword("password")
                .createIceServer(),

        )


        peerConnectionFactory= createPeerFactory()!!


        /**
         * Starting the camera if permission is granted else requesting for permission
         */
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askPermission()
        }else{
//            localView.isEnabled = true
            isFront = true
            showCamera()
        }

        ivMessage.setOnClickListener {
            val commonChat = CommonChatFragment()
            commonChat.show(supportFragmentManager, CommonChatFragment.TAG)
        }

    }

    /**
     * SnackBar Code
     */

    private fun showSnack(message: CommonChatPayload){
        customSnackView.tvUser.text = message.user_id
        customSnackView.tvText.text = message.message
        snackbar.show()
    }

    private fun setupCustomSnackBar(){
        snackViewBinding = CommonChatSnackviewBinding.inflate(layoutInflater)
        snackbar = Snackbar.make(snackViewBinding.root, "", Snackbar.LENGTH_LONG)
        customSnackView = snackViewBinding
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)
        val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
        val params : FrameLayout.LayoutParams= snackbar.view.layoutParams as FrameLayout.LayoutParams
        val width = Resources.getSystem().displayMetrics.widthPixels * 0.6
        params.width = width.toInt()
        snackbarLayout.layoutParams = params
        snackbarLayout.addView(customSnackView.root, 0)
    }


    /**
    * Creating PeerConnectionFactory to create PeerConnection, establishing peer to peer connection
    */
    fun createPeerFactory(): PeerConnectionFactory? {
        eglBaseContext=EglBase.create().eglBaseContext
        val options = InitializationOptions.builder(applicationContext)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext,
                    true,
                    true
                )
            ).createPeerConnectionFactory()
    }


    /**
     * Handling the permissions for the user camera
     */
    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            ),
            CAPTURE_PERMISSION_REQUEST_CODE
        )
    }

    /**
     * Starting camera when permissions are granted
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAPTURE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCamera()
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }



    /**
     * capturing the camera video stream and adding it to the mediaStream with peerConnectionFactory
     */
    private fun showCamera() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askPermission()
            return
        }
        recyclerDataArrayList.remove(userData)

        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
        // create VideoCapturer
        val videoCapturer = createCameraCapturer(isFront)
        val videoSource = peerConnectionFactory.createVideoSource(
            false
        )
        videoCapturer!!.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource.capturerObserver
        )
        videoCapturer.startCapture(480, 640, 30)

        videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)


        localAudioTrack = peerConnectionFactory.createAudioTrack("100" + "_audio", audioSource)

        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream")
        mediaStream.addTrack(videoTrack)
        mediaStream.addTrack(localAudioTrack)

        userData = data("userLocal", 0, videoTrack!!, true, eglBaseContext, null)
        recyclerDataArrayList.add(userData!!)
        setUpEpoxy()
        SignalingClient.get()?.init(this, rname)

    }

    /**
     * creating and returning camera capture accoring to the camera used
     */
    private fun createCameraCapturer(isFront: Boolean): VideoCapturer? {
        val enumerator = Camera1Enumerator(false)
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        for (deviceName in deviceNames) {
            if (if (isFront) enumerator.isFrontFacing(deviceName) else enumerator.isBackFacing(
                    deviceName
                )
            ) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    /**
     * Handling the permission for the screen share or starting the service for the same
     */
    @TargetApi(21)
    private fun startScreenCapture() {
        if(Build.VERSION.SDK_INT >=28) {
            // Start a foreground service and post notification regarding the screen share
            val intent = Intent(this, MediaProjectionService::class.java)
            startForegroundService(intent)
        }

        val mediaProjectionManager = application.getSystemService(
            Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        // Get Permission for screen sharing
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE)
    }

    /**
     * For the case of screen capture handling the Intent result
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
        {
            Toast.makeText(this,"permission not granted",Toast.LENGTH_SHORT).show()
            return;
        }else {
            mMediaProjectionPermissionResultCode = resultCode
            mMediaProjectionPermissionResultData = data
            screenCast()
        }
        if (data != null && (requestCode == REQUEST_CODE_STREAM && resultCode == RESULT_OK)
        ) {
            mMediaProjectionPermissionResultCode = resultCode
            mMediaProjectionPermissionResultData = data
            val displayService: MediaProjectionService? = MediaProjectionService.INSTANCE
            val endpoint: String = endPointUrlBase+"app/{$rname}"
            displayService?.prepareStreamRtp(endpoint, resultCode, data)
            displayService?.startStreamRtp(endpoint)
        }
    }

    /**
     * capturing the screen cast stream and adding it to the mediaStream with peerConnectionFactory
     */
    private fun screenCast() {
        // create Video Capturer
        if(userDataScreen!=null){
            recyclerDataArrayList.remove(userDataScreen)
        }
        val videoCapturer = createScreenCapturer()

        val surfaceTextureHelper = SurfaceTextureHelper.create("ScreenThread", eglBaseContext)

        val videoSource = peerConnectionFactory.createVideoSource(
            videoCapturer!!.isScreencast
        )
        videoCapturer.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource.capturerObserver
        )

        videoCapturer.startCapture(480, 640, 30)
        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        videoTrackScreen = peerConnectionFactory.createVideoTrack("100", videoSource)
        videoTrackScreen?.setEnabled(true)

        localAudioTrackScreen = peerConnectionFactory.createAudioTrack("100" + "_audio", audioSource)

        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream")
        mediaStream.addTrack(videoTrackScreen)
        mediaStream.addTrack(localAudioTrackScreen)
        userDataScreen = data("userScreen", 0, videoTrackScreen!!, true, eglBaseContext, null)
        recyclerDataArrayList.add(userDataScreen!!)
        setUpEpoxy()

        SignalingClient.get()?.init(this,rname)
    }

    /**
     * Returning the screen Capturer
     */
    @TargetApi(21)
    private fun createScreenCapturer(): VideoCapturer? {
        if (mMediaProjectionPermissionResultCode !== Activity.RESULT_OK) {
            report("User didn't give permission to capture the screen.")
            return null
        }
        return ScreenCapturerAndroid(mMediaProjectionPermissionResultData, object : MediaProjection.Callback() {
            override fun onStop() {
                videoTrackScreen?.setEnabled(false)
                report("Screen Share stopped")
            }

        })
    }

    /**
     * Handling the epoxy with the case of array size
     * of array size is small than the grid is with single span count
     * adding the data to the epoxy controller to display the videoTrack on UI thread
     */

    private fun setUpEpoxy() {
        val layoutManager: GridLayoutManager
        if (recyclerDataArrayList.size == 1 || recyclerDataArrayList.size == 2) {
            Log.d("recyclerDataArrayList", "size ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager(this, 1)
        } else if (recyclerDataArrayList.size % 2 == 1) {
            Log.d("recyclerDataArrayList", "size 3 ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager(this, 2)
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    when (position) {
                        0 -> return 2
                        else -> return 1
                    }
                }
            }
        } else {
            Log.d("recyclerDataArrayList", "size ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager(this, 2)
        }
        /**
         * Setting values to the epoxy controller
         */
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels - 20
        val controller = EpoxyController(screenHeight, screenWidth, applicationContext)
        controller.images = recyclerDataArrayList
        val epoxyRecyclerView: EpoxyRecyclerView = findViewById<EpoxyRecyclerView>(R.id.idCourseRV)
        runOnUiThread() {
            epoxyRecyclerView.layoutManager = layoutManager
            epoxyRecyclerView.setController(controller)
        }
    }

    fun createVideoPath(context: Context, fileName: String?): String? {
        val imageThumbsDirectory: File = context.getExternalFilesDir("FOLDER")!!
        if (!imageThumbsDirectory.exists()) {
            imageThumbsDirectory.mkdir()
        }
        val appDir: String = context.getExternalFilesDir("FOLDER")!!.absolutePath
        val file = File(appDir, fileName)
        return file.absolutePath
    }


    /**
     * Creating the PeerConnection to establish connection between two devices
     */
    @Synchronized
    private fun getOrCreatePeerConnection(socketId: String): PeerConnection {
        var peerConnection = peerConnectionMap[socketId]
        if (peerConnection != null) {
            return peerConnection
        }
        peerConnection =
            peerConnectionFactory.createPeerConnection(iceServers, object : PeerConnectionAdapter(
                "PC:$socketId"
            ) {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate)
                    SignalingClient.get()?.sendIceCandidate(iceCandidate, socketId)
                }

                /**
                 * Adding new data to the recyclerDataArrayList whenever new mediaStream is added to the network
                 */
                override fun onAddStream(mediaStream: MediaStream) {
                    super.onAddStream(mediaStream)

                    fileName = createVideoPath(applicationContext, fileName)
                    remoteVideoTrack = mediaStream.videoTracks[0]
                    recyclerDataArrayList.add(
                        data(
                            "user",
                            remoteViewsIndex++,
                            mediaStream.videoTracks[0],
                            false,
                            eglBaseContext,
                            null
                        )
                    )
                    setUpEpoxy()
                    Log.e("stream", "stream added ${mediaStream.videoTracks.size}")
                    Log.e("path", createVideoPath(applicationContext, fileName).toString())
                }


            })
        peerConnection!!.addStream(mediaStream)
        peerConnectionMap[socketId] = peerConnection
        return peerConnection
    }


    /**
     * Handling the peer connection
     * Implementing the SignalClient Callback Interface functions
     */

    override fun onCreateRoom(id: String) {
    }


    /**
     * Creating the Peer Connection
     */

    override fun onPeerJoined(socketId: String) {
        val peerConnection = getOrCreatePeerConnection(socketId)
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection.createOffer(object : SdpAdapter("createOfferSdp:$socketId") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peerConnection.setLocalDescription(
                    SdpAdapter("setLocalSdp:$socketId"),
                    sessionDescription
                )
                SignalingClient.get()?.sendSessionDescription(sessionDescription, socketId,"ok")
            }
        }, constraints)
    }

    override fun onSelfJoined() {

    }

    override fun onPeerLeave(data: String) {

    }


    /**
     * Initiates the creation of an SDP offer for the purpose of starting a new WebRTC connection to a remote peer.
     */
    override fun onOfferReceived(data: JSONObject) {
        runBlocking {

            val socketId = data.optString("from")
            val peerConnection = getOrCreatePeerConnection(socketId)
            peerConnection.setRemoteDescription(
                SdpAdapter("setRemoteSdp:$socketId"),
                SessionDescription(SessionDescription.Type.OFFER, data.optString("sdp"))
            )
            val constraints = MediaConstraints().apply {
                mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
            }

            /**
             * Initiates the creation an SDP answer to an offer received from a remote peer
             * during the offer/answer negotiation of a WebRTC connection.
             */
            peerConnection.createAnswer(object : SdpAdapter("localAnswerSdp") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    super.onCreateSuccess(sessionDescription)
                    peerConnection.setLocalDescription(SdpAdapter("setLocalSdp:$socketId"), sessionDescription)
                    SignalingClient.get()?.sendSessionDescription(sessionDescription, socketId,"ok")
                }
            }, constraints)
        }
    }


    /**
     *
     */
    override fun onAnswerReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection.setRemoteDescription(
            SdpAdapter("setRemoteASdp:$socketId"),
            SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp"))
        )
        if(data.optString("part")=="not ok")
        {
            recyclerDataArrayList.removeLast()
            setUpEpoxy()
        }
    }

    /**
     *
     */

    override fun onIceCandidateReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection.addIceCandidate(
            IceCandidate(
                data.optString("id"),
                data.optInt("label"),
                data.optString("candidate")
            )
        )

        Log.e("ice candidate","Added ice candidate")
    }


    /**
     *
     * UI Button Click handled
     *
     *
     */


    // Handling the video switch
    fun switchVideo(it: ImageView) {
        if(count%2 !=0) {
            it.setImage(R.drawable.ic_addvideo)
            count++
            videoTrack?.setEnabled(true)
        }else{
            it.setImage(R.drawable.ic_removevideo)
            count++
            videoTrack?.setEnabled(false)
        }
//        localView.isEnabled = !(localView.isEnabled)
    }

    //handling the audio switch
    fun switchAudio(it: ImageView) {
        if (localAudioTrack != null) {
            if (localAudioTrack?.enabled() == true) {
                localAudioTrack?.setEnabled(false)
                it.setImage(R.drawable.ic_removeaudio)
            }
            else {
                localAudioTrack?.setEnabled(true)
                it.setImage(R.drawable.ic_addaudio)
            }
        }
    }

    // handling the camera used either front or back
    fun switchCamera(it: ImageView){
        recyclerDataArrayList.remove(userData)
        isFront = !isFront
        showCamera()
    }

    // handling the screen share button click function
    private fun onScreenShare() {

        if(!isScreenShare){
            startScreenCapture()
            screenShare.let {
                it.setTextColor(R.color.quantum_black_100)
                it.background = ContextCompat.getDrawable(this,R.drawable.circular_button_view_without_border)
            }
            isScreenShare=true
        }else{
            videoTrackScreen?.setEnabled(false)
            localAudioTrackScreen?.setEnabled(false)
            isScreenShare = false
            setUpEpoxy()
        }
    }


    // ending the call
    override fun onBackPressed() {
        super.onBackPressed()
        endcall()
        overridePendingTransition(R.anim.slide_in_animation, R.anim.slide_out_animation)
    }


    // on activity destroyed 
    override fun onDestroy() {
        super.onDestroy()
        CommonChatSocketHandler.closeConnection()
        CommonChatSocketHandler.mSocket.close()
        if (isScreenShare){
            endcall()
            SignalingClient.get()?.destroy()
        }
        val displayService: MediaProjectionService? = MediaProjectionService.INSTANCE
        if (displayService != null && !displayService.isStreaming() && !displayService.isRecording()) {
            //stop service only if no streaming or recording
            stopService(Intent(this, MediaProjectionService::class.java))
        }
    }


    // disabling all the running tracks
    private fun endcall() {
        if (peerConnectionMap.isNotEmpty()) {
            for (i in peerConnectionMap.values) {
                i.close()
            }
        }
        if (this::remoteVideoTrack.isInitialized){
            remoteVideoTrack.removeSink(fileRenderer)
            videoTrack?.setEnabled(false)
            videoTrackScreen?.setEnabled(false)
            localAudioTrackScreen?.setEnabled(false)
            localAudioTrack?.setEnabled(false)
            fileRenderer.release()
        }

    }


    companion object {
        private val KEY = "AKIASGPFZLBMV25FCLWJ"
        private val SECRET = "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"
        private val BUCKET="kurento-recorded-files"
        val clientConfiguration = ClientConfiguration()
        val regions = Regions.fromName("ap-south-1")
        private var s3Client: AmazonS3Client? = null
        private var credentials: BasicAWSCredentials=BasicAWSCredentials(KEY, SECRET)

        //track Choosing Image Intent
        private val CHOOSING_IMAGE_REQUEST = 1234

        private var fileUri: Uri? = null
        private var bitmap: Bitmap? = null
    }


    fun onConnectionStartedRtmp(rtmpUrl: String?) {}

    fun onConnectionSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@GridActivity,
                "Connection success",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onConnectionFailedRtmp(reason: String) {
        runOnUiThread {
            Toast.makeText(
                this@GridActivity,
                "Connection failed. $reason", Toast.LENGTH_SHORT
            )
                .show()
            MediaProjectionService.INSTANCE?.stopStream()
        }
    }

    fun onNewBitrateRtmp(bitrate: Long) {}

    fun onDisconnectRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@GridActivity,
                "Disconnected",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onAuthErrorRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@GridActivity,
                "Auth error",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun onAuthSuccessRtmp() {
        runOnUiThread {
            Toast.makeText(
                this@GridActivity,
                "Auth success",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // Creating and sharing the room link on socail media
    private fun createRoomLink() {
        Log.d("Intent","The intent that I am looking at is called")
        val shareBody="https://www.myworld.com/liveRoom?code=$rname"
        val bottomSheetDialog=BottomSheetDialog(this)
        val link=bottomSheetDialog.findViewById<TextView>(R.id.textView16)
        link?.setText(shareBody)
       //TODO To be changed
        // bottomSheetDialog.setContentView(R.layout.share_myworld)
        val pm: PackageManager = this.packageManager
        val finalLaunchables:MutableList<ResolveInfo> =ArrayList()
        val recyclerView=bottomSheetDialog.findViewById<RecyclerView>(R.id.myrecview)
        recAdapter=RecAdapter(pm,finalLaunchables,applicationContext,shareBody)
        recyclerView?.adapter=recAdapter
        val linearLayoutManager: LinearLayoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL,false)
        recyclerView?.layoutManager = linearLayoutManager
        val main = Intent(Intent.ACTION_MAIN,null)
        main.addCategory(Intent.CATEGORY_LAUNCHER)
        val launchables: List<ResolveInfo> = pm.queryIntentActivities(main, 0)
        for (resolveInfo in launchables){
            val packageName=resolveInfo.activityInfo.packageName
            if (packageName.contains("om.viber.voip") ||
                packageName.contains("com.twitter.android") ||
                packageName.contains("com.google.android.gm") ||
                packageName.contains("com.whatsapp") ||
                packageName.contains("com.facebook.katana") ||
                packageName.contains("com.instagram.android")
            ) {
                finalLaunchables.add(resolveInfo)
            }
        }
        Collections.sort(
            finalLaunchables,
            ResolveInfo.DisplayNameComparator(pm)
        )
        runOnUiThread {
            recAdapter.notifyDataSetChanged()
        }
//        if(recAdapter.)
        bottomSheetDialog.show()
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        showDeepLinkOffer(appLinkAction, appLinkData)
    }

    private fun showDeepLinkOffer(appLinkAction: String?, appLinkData: Uri?) {
        // 1
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            // 2
            val rCode = appLinkData.getQueryParameter("code")
            Log.e("deepLink",rCode.toString())
            if (rCode.isNullOrBlank().not()) {
                rname=rCode.toString()
            }
        } else {
        }
    }


    private fun createSDPConstraints(): MediaConstraints? {

        val constraints = MediaConstraints()
        constraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"))
        constraints.mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", "true"))
        constraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "maxFrameRate",
                60.toString()
            )
        )
        constraints.mandatory.add(
            MediaConstraints.KeyValuePair(
                "minFrameRate",
                1.toString()
            )
        )
        return constraints
    }
    private fun createFile(context: Context, srcUri: Uri?, dstFile: File) {
        try {
            val inputStream = srcUri?.let { context.contentResolver.openInputStream(it) } ?: return
            val outputStream = FileOutputStream(dstFile)
            IOUtils.copy(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    private fun uploadFile(){
        val upload=object:Thread(){
            override fun run() {
                clientConfiguration.maxErrorRetry = 1
                clientConfiguration.connectionTimeout = 3600000
                clientConfiguration.socketTimeout = 3600000

                s3Client = AmazonS3Client(credentials)
                s3Client!!.setRegion(Region.getRegion(regions))

                TransferNetworkLossHandler.getInstance(applicationContext)
                val transferUtility = TransferUtility.builder()
                    .context(applicationContext)
                    .s3Client(s3Client)
                    .awsConfiguration(AWSConfiguration(JSONObject(awsconfig)))
                    .build()

                val listener = object: TransferListener {
                    override fun onProgressChanged(id: Int, curr: Long, tot: Long) {
                        Log.e("progress",curr.toString())
                    }
                    override fun onStateChanged(id: Int, state: TransferState?) {
                        when (state) {
                            TransferState.COMPLETED -> { Log.e("Demo", "Upload succeeded.") }
                            TransferState.FAILED -> { Log.e("Demo", "Upload failed.")/* handle err */ }
                            TransferState.WAITING-> { Log.e("Demo", "Upload waiting.") }

                            TransferState.IN_PROGRESS-> { Log.e("Demo", "Upload in progress.") }

                            TransferState.PAUSED-> { Log.e("Demo", "Upload paused.") }

                            TransferState.RESUMED_WAITING-> { Log.e("Demo", "Upload resume waiting.") }

                            TransferState.CANCELED-> { Log.e("Demo", "Upload cancel.") }

                            TransferState.WAITING_FOR_NETWORK-> { Log.e("Demo", "Upload wait for network.") }

                            TransferState.PART_COMPLETED-> { Log.e("Demo", "Upload part completed.") }

                            TransferState.PENDING_CANCEL-> { Log.e("Demo", "Upload pending cancel.") }

                            TransferState.PENDING_PAUSE-> { Log.e("Demo", "Upload pending pause.") }

                            TransferState.PENDING_NETWORK_DISCONNECT-> { Log.e("Demo", "Upload pending network disconnect.") }

                            TransferState.UNKNOWN-> { Log.e("Demo", "Upload unknown.") }

                            else -> { Log.e("Demo", "Upload btw.")
                                /* handle cases... */ }

                        }
                    }
                    override fun onError(id: Int, ex: Exception?) { /* handle err */ }
                }
                val no=(0..10).random()
                transferUtility.upload(BUCKET,"$rname - {$no}",File(fileName))
                    .setTransferListener(listener)
            }
        }
        upload.start()

    }




}

internal class RecAdapter(pm : PackageManager?,private val apps: List<ResolveInfo?>,val context: Context?,val link:String):
    RecyclerView.Adapter<RecAdapter.MyViewHolder>(){
    internal class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val label = view.findViewById<View>(R.id.mylabel) as TextView
        var icon: ImageView = view.findViewById<View>(R.id.myicon) as ImageView
    }
    private var pm: PackageManager? = null
    override fun getItemCount(): Int {
        return apps.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.myrow, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val label= apps[position]?.loadLabel(pm)
        holder.label.text = label
        val icon=apps.get(position)?.loadIcon(pm)
        holder.icon.setImageDrawable(icon)
        holder.itemView.setOnClickListener {
            val launchable=apps.get(position) as ResolveInfo
            val activity: ActivityInfo = launchable.activityInfo
            val i = Intent(Intent.ACTION_SEND)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.type="text/plain"
            val packageName=activity.packageName
            i.putExtra(Intent.EXTRA_TEXT,link)
            i.setPackage(packageName)
            context?.startActivity(i)
        }
    }
    init {
        this.pm=pm
    }



}
