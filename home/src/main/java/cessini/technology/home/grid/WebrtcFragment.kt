package cessini.technology.home.grid

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.commonui.activity.EpoxyController
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.activity.data
import cessini.technology.commonui.activity.live.PeerConnectionAdapter
import cessini.technology.commonui.activity.live.SdpAdapter
import cessini.technology.commonui.activity.live.SignalingClient
import cessini.technology.home.controller.HomeEpoxyController
import cessini.technology.home.databinding.FragmentWebrtcBinding
import cessini.technology.model.ViewerX
import cessini.technology.myspace.R
import cessini.technology.newapi.model.CreatorListeners
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.webrtc.*
import javax.inject.Inject

@AndroidEntryPoint
class WebrtcFragment : Fragment(), SignalingClient.Callback {

    companion object {
        private const val ARG_R_NAME = "ARG_R_NAME"

        fun createInstance(rname: String, liked: HashMap<String, Boolean>) =
            WebrtcFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_R_NAME, rname)
                }

                // FIXME: This is not okay; the map contents will be lost when the fragment reloads
                // Ideally, such view state should be stored in view model
                liked.putAll(liked)
            }
    }

    private lateinit var rname: String
    private val liked: HashMap<String, Boolean> = HashMap()

    private lateinit var viewModel: GridViewModel
    private lateinit var binding: FragmentWebrtcBinding

    // FIXME: These must not be created by or referenced in fragments
    lateinit var userIdentifierPreferences: UserIdentifierPreferences
    lateinit var authPreferences: AuthPreferences

    //    private lateinit var roomPagerAdapter: RoomPagerAdapter
    var roomList: ArrayList<ArrayList<CreatorListeners>> = ArrayList()
    var testingList = mutableListOf<ViewerX>()
    private lateinit var remoteVideoTrack: VideoTrack
    private lateinit var eglBaseContext: EglBase.Context
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var localView: SurfaceViewRenderer
    private lateinit var mediaStream: MediaStream
    private lateinit var iceServers: List<PeerConnection.IceServer>
    private lateinit var peerConnectionMap: HashMap<String, PeerConnection>
    private lateinit var  remoteViews: Array<SurfaceViewRenderer>
    var remoteViewsIndex = 1
    lateinit var layoutManager: GridLayoutManager
    lateinit var controller: EpoxyController
    lateinit var videoTrack: VideoTrack
    val recyclerDataArrayList:ArrayList<data> = ArrayList()
    val recyclerHomeDataArrayList: ArrayList<home_data> =ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWebrtcBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rname = arguments?.getString(ARG_R_NAME)
            ?: error("WebrtcFragment should not be created without setting ARG_R_NAME")

        authPreferences = AuthPreferences(requireActivity().applicationContext)
        userIdentifierPreferences = UserIdentifierPreferences(
            requireActivity().applicationContext,
            authPreferences
        )

        peerConnectionMap = HashMap()
        viewModel = ViewModelProvider(this).get(GridViewModel::class.java)
        iceServers = ArrayList()
        (iceServers as ArrayList<PeerConnection.IceServer>).add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
//        iceServers = listOf(
//            PeerConnection.IceServer.builder("stun:3.108.54.21:3478")
//                .createIceServer(),
//            PeerConnection.IceServer.builder("turn:3.108.54.21:3478")
//                .setUsername("Admin")
//                .setPassword("password")
//                .createIceServer(),
//            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
//        )


//        Log.e("chao", "room created:")

        println("RUNNING")
        eglBaseContext = EglBase.create().eglBaseContext

        // create PeerConnectionFactory

        peerConnectionFactory= createPeerFactory()!!
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext)
//        // create VideoCapturer
//        val videoCapturer = createCameraCapturer(true)
        val videoSource = peerConnectionFactory.createVideoSource(
            false
        )
//        videoCapturer!!.initialize(
//            surfaceTextureHelper,
//            (activity as HomeActivity),
//            videoSource.capturerObserver
//        )
//        videoCapturer.startCapture(480, 640, 30)

//        localView = findViewById(R.id.localView)
//        localView.setMirror(true)
//        localView.init(eglBaseContext, null)
        // create VideoTrack
        videoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        // display in localView
//        videoTrack.addSink(localView)
        // audio track
//        remoteViews = arrayOf(
//            findViewById(R.id.remoteView),
//            findViewById(R.id.remoteView2),
//            findViewById(R.id.remoteView3)
//        )
//        for (remoteView in remoteViews) {
//            remoteView.setMirror(false)
//            remoteView.init(eglBaseContext, null)
//        }

        mediaStream = peerConnectionFactory.createLocalMediaStream("mediaStream")
        mediaStream.addTrack(videoTrack)
//        recyclerDataArrayList.add(data("user",0,videoTrack,false,eglBaseContext,null))
//        setupepoxy()
        cessini.technology.commonui.activity.live.SignalingClient.get()?.init(this, rname)

//        if(recyclerDataArrayList.size==0)
//        {
//            binding.noroom.visibility=VISIBLE
//            binding.linearLayout2.visibility=GONE
//        }
//        else{
//            binding.noroom.visibility=GONE
//            binding.linearLayout2.visibility= VISIBLE
//
//        }
        binding.linearLayout2.setOnClickListener {
            val intent: Intent = Intent()

            intent.setClassName(requireContext(),"cessini.technology.commonui.activity.GridActivity")
//            intent.setClassName(requireContext(),"cessini.technology.myspace.live.LiveMyspaceActivity")
            intent.putExtra("Room Name","${rname}")
            startActivity(intent)

        }

        /*TODO: setLike(binding.imageView23,rname)*/


        /*TODO:binding.imageView22.setOnClickListener {
            val shareBody="https://www.myworld.com/liveRoom?code=$rname"
            shareText(shareBody)
        }*/

        /*TODO:binding.imageView22.setOnClickListener {

            val shareBody="https://www.myworld.com/liveRoom?code=$rname"
            val bundle = Bundle()
            bundle.putString("sharelink", shareBody)
            findNavController().navigate(R.id.action_global_share,bundle)
        }*/


        /*TODO:binding.imageView23.setOnClickListener {
            setLikedClicked(rname)
        }*/
    }

    fun createPeerFactory(): PeerConnectionFactory? {
        val options = PeerConnectionFactory.InitializationOptions.builder((activity as HomeActivity))
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
        return PeerConnectionFactory.builder()
            .setOptions(PeerConnectionFactory.Options())
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .setVideoEncoderFactory(
                DefaultVideoEncoderFactory(
                    eglBaseContext,
                    true,
                    true
                )
            )
            .createPeerConnectionFactory()
    }
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

    private fun setLike(button1: ImageView, title: String) {
        if(liked.get(title)!=null && liked.get(title)==true){
            button1.setImageResource(cessini.technology.home.R.drawable.ic_likeactive)
        }
        else {
            button1.setImageResource(cessini.technology.home.R.drawable.ic_like_inactive)
        }
    }

    /*TODO:private fun setLikedClicked(title: String) {
        if(userIdentifierPreferences.loggedIn) {
            if (liked.get(title) != null && liked.get(title) == true) {
                viewModel.likeRoom(title, userIdentifierPreferences.id)
                binding.imageView23.setImageResource(cessini.technology.home.R.drawable.ic_like_inactive)
                liked.put(title, false)
            } else {
                viewModel.likeRoom(title, userIdentifierPreferences.id)
                binding.imageView23.setImageResource(cessini.technology.home.R.drawable.ic_likeactive)
                liked.put(title, true)
            }
        }
    }*/

    fun setupepoxy() {
        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels
        val layoutManager: GridLayoutManager
        if (recyclerDataArrayList.size == 1 || recyclerDataArrayList.size == 2) {
            Log.e("recyclerDataArrayList", "size ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager((activity as HomeActivity), 1)
        } else if (recyclerDataArrayList.size % 2 == 1) {
            Log.e("recyclerDataArrayList", "size 3 ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager(
                (activity
                        as HomeActivity), 2
            )
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    when (position) {
                        0 -> return 2
                        else -> return 1
                    }
                }
            }
        } else {
            Log.e("recyclerDataArrayList", "size ${recyclerDataArrayList.size}")
            layoutManager = GridLayoutManager((activity as HomeActivity), 2)
        }
//        val controller = EpoxyController(screenHeight, screenWidth, (activity as HomeActivity))
        /*val homeController=HomeEpoxyController(requireContext())
        homeController.streams= (recyclerHomeDataArrayList)
//        controller.images = (recyclerDataArrayList)
//        homeController.streams=
        (activity as HomeActivity).runOnUiThread {
            binding.recyclerView.layoutManager = layoutManager
            binding.recyclerView.setController(homeController)
        }*/
    }

    override fun onStart() {
        super.onStart()
//        viewModel = ViewModelProvider(this).get(GridViewModel::class.java)
//        viewModel.getTimeLiveRooms()
//        observeList()
//        initializeViews()
//        observeData()
//        viewModel.getRoomViews()
//        Log.d("roomviews","inside grid fragment ${viewModel.viewerList}")
    }

    private fun initializeViews() {
//        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
//        val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels
//         val fragmentManager= parentFragmentManager

        ///////////////////////////////////////////////////////////////////////////////////////////////
//        Log.d("roomviews","inside initialise ${observeList()}")
//        viewModel.testList.observe(viewLifecycleOwner) {
//            Log.d("roomviews", "inside observerData $it")
//            testingList=it
//            roomPagerAdapter =
//            RoomPagerAdapter(requireActivity().applicationContext, requireActivity(),fragmentManager, screenWidth, screenHeight - 250,rname,peerConnectionFactory,peerConnectionMap,iceServers,videoTrack,mediaStream,eglBaseContext,RecyclerDataArrayList)
//            binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
//            binding.viewPager.adapter = roomPagerAdapter
//            viewModel.getLiveRooms().observe(viewLifecycleOwner) {
//                passMessageToViewPager(it.message)
//            }
//            observeData()
//            roomPagerAdapter.notifyDataSetChanged()

//        }
//        roomPagerAdapter =
//            RoomPagerAdapter(roomList, requireContext(), requireActivity(),fragmentManager, screenWidth, screenHeight - 250)
//        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
//        binding.viewPager.adapter = roomPagerAdapter
//        observeData()
    }
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
                    cessini.technology.commonui.activity.live.SignalingClient.get()?.sendIceCandidate(iceCandidate, socketId)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    super.onAddStream(mediaStream)
//                    mediaStream.videoTracks[0].addSink(
//                        remoteViews[remoteViewsIndex++]
//                    )
                    recyclerDataArrayList.add(data("user", remoteViewsIndex++, mediaStream.videoTracks[0], false, eglBaseContext, null))
                    setupepoxy()
                    Log.e("stream","stream added ${mediaStream.videoTracks.size}")
                }


            })
        peerConnection!!.addStream(mediaStream)

        peerConnectionMap[socketId] = peerConnection
        return peerConnection
    }

//    private fun setupexpoxy() {
//        recyclerDataArrayList.clear()
//        recyclerDataArrayList.addAll(newRecyclerDataArrayList)
//        this.notifyDataSetChanged()
//
//        // at last set adapter to recycler view.
////        val screenWidth: Int = Resources.getSystem().displayMetrics.widthPixels
////        val screenHeight: Int = Resources.getSystem().displayMetrics.heightPixels - 20
//
//    }

    override fun onCreateRoom(id: String) {
    }

    override fun onPeerJoined(socketId: String) {
        val peerConnection = getOrCreatePeerConnection(socketId)
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }
        peerConnection.createOffer(object : SdpAdapter("createOfferSdp:$socketId") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                peerConnection.setLocalDescription(
                    SdpAdapter("setLocalSdp:$socketId"),
                    sessionDescription
                )
                cessini.technology.commonui.activity.live.SignalingClient.get()?.sendSessionDescription(sessionDescription, socketId,"ok")

            }
        }, constraints)
    }

    override fun onSelfJoined() {}
    override fun onPeerLeave(data: String) {

    }



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
            peerConnection.createAnswer(object : SdpAdapter("localAnswerSdp") {
                override fun onCreateSuccess(sessionDescription: SessionDescription) {
                    super.onCreateSuccess(sessionDescription)
                    peerConnection.setLocalDescription(SdpAdapter("setLocalSdp:$socketId"), sessionDescription)
                    SignalingClient.get()?.sendSessionDescription(sessionDescription, socketId,"not ok")
                }
            }, constraints)
        }
    }

    override fun onAnswerReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection.setRemoteDescription(
            SdpAdapter("setRemoteASdp:$socketId"),
            SessionDescription(SessionDescription.Type.ANSWER, data.optString("sdp"))
        )
    }

    override fun onIceCandidateReceived(data: JSONObject) {
        val socketId = data.optString("from")
        val peerConnection = getOrCreatePeerConnection(socketId)
        peerConnection.addIceCandidate(
            IceCandidate(
                data.optString("id"),
                data.optInt("label"),
                data.optString("candidate"),

            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for(i in peerConnectionMap.values)
            i.close()
    }
    //    private fun observeList(): MutableList<ViewerX> {
//        viewModel.testList.observe(viewLifecycleOwner) {
//            Log.d("roomviews", "inside observerData $it")
//            testingList=it
//        }
//        return testingList
//    }
//
//    private fun observeData() {
//        viewModel.getLiveRooms().observe(viewLifecycleOwner) {
//            passMessageToViewPager(it.message)
//        }
//    }
//
//
//    private fun passMessageToViewPager(message: List<Message>?) {
//        if (message == null) {
//            return;
//        }
//        roomList.clear()
//        for (i in 0..message.size - 1) {
//            var creatorListenerList: ArrayList<CreatorListeners> = ArrayList()
//            val creator = message[i].creator
//            val listeners = message[i].listeners
//            creatorListenerList.add(
//                CreatorListeners(
//                    isCreator = true,
//                    _id = creator._id,
//                    channel_name = creator.channel_name,
//                    name = creator.name,
//                    profile_picture = creator.profile_picture
//                )
//            )
//
//            if (listeners != null) {
//                for (i in listeners.indices) {
//                    creatorListenerList.add(
//                        CreatorListeners(
//                            isCreator = false,
//                            _id = listeners[i]._id,
//                            channel_name = listeners[i].channel_name,
//                            name = listeners[i].name,
//                            profile_picture = listeners[i].profile_picture
//                        )
//                    )
//                }
//            }
//            roomList.add(creatorListenerList)
//        }
////        roomPagerAdapter.notifyDataSetChanged()
//    }
}