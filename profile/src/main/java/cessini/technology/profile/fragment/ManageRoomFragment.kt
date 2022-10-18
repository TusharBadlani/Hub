package cessini.technology.profile.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.common.dateTime
import cessini.technology.commonui.common.navigateToProfile
import cessini.technology.commonui.common.toast
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.model.CreateRoomRequest
import cessini.technology.model.RequestProfile
import cessini.technology.model.Room
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.myspace.access.MySpaceFragmentViewModel
import cessini.technology.myspace.create.CreateRoomFragment
import cessini.technology.myspace.create.CreateRoomSharedViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.Navigator
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.profile.*
import cessini.technology.profile.`class`.ChatUserOther
import cessini.technology.profile.chatSocket.SocketHandler
import cessini.technology.profile.databinding.FragmentManageRoomBinding
import com.airbnb.epoxy.EpoxyController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_manage_room.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class ManageRoomFragment :
    BaseBottomSheet<FragmentManageRoomBinding>(R.layout.fragment_manage_room),
    BottomSheetLevelInterface {

    companion object {
        private const val TAG = "ManageRoomFragment"
    }

    private val viewModel: MySpaceFragmentViewModel by activityViewModels()
    private val baseViewModel by activityViewModels<BaseViewModel>()
    private val roomSharedViewModel:CreateRoomSharedViewModel by activityViewModels()

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    @Inject
    lateinit var roomRepository: RoomRepository

    private val acceptedRequets: MutableMap<String, MutableList<String>> = mutableMapOf()

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintLayout5.transitionToStart()

        roomSharedViewModel.bottomSheetDraggedState.observe(viewLifecycleOwner) { state->
            val shiftedState = (1+state)/2f
            setMotionLevel(-shiftedState)
            binding.constraintLayout5.progress = shiftedState
        }

        binding.createRoomButton.setOnClickListener {
            val createRoomFragment = CreateRoomFragment(this)
            createRoomFragment.show(parentFragmentManager, createRoomFragment.tag)
        }

        binding.backNavigationButton.setOnClickListener {
            dismiss()
        }

        //socket
        SocketHandler.setSocket()
        val mSocket = SocketHandler.getSocket()
        val chatUser1 = ChatUserOther("subhrapriyadarshinee").getJSONuser()


        mSocket.connect()


        observeMySpaceFragment()




        runCatching { fetchRooms() }
        runCatching { fetchRoomMessages() }


//        mSocket.on("get-messages-response"){
//            Log.d(TAG,"get-messages-response size ${it.size}")
//            Log.d(TAG,"get-messages-response ${it[0]}")
//
//        }
        mSocket.on("get-messages-list-response") {
            Log.d(TAG, "get-messages-list-response ${it[0]}")
        }


        mSocket.on("connect") {
            Log.d(TAG, "connected")
            mSocket.emit("get-messages-list", chatUser1)
//            mSocket.emit("get-messages",getMessage(userOne,"subhrapriyadarshinee").getMessages())
//            runBlocking {
//                launch {
//                    mSocket.emit("get-messages",getMessage(userOne,"subhrapriyadarshinee").getMessages())
//                }
//            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight)
        }
        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 != 0) {
                        dialog.dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit

        })
        dialog.behavior.isFitToContents = true
        dialog.behavior.isDraggable = false
        return dialog
    }

    private fun observeMySpaceFragment() {
        viewModel.mySpaceFragmentState.observe(viewLifecycleOwner) {
            if (it == -3) {
                onSheet2Dismissed()
            }
            if (it == -2) {
                onSheet1Dismissed()
            }
        }
    }


    private fun fetchRooms() {
        Log.e(TAG, "Inside fetchRooms()")
        val roomsDeffered = viewLifecycleOwner.lifecycleScope.async {
            Log.e(
                TAG,
                "fetchRooms: ${profileRepository.getProfileRooms(userIdentifierPreferences.id)}"
            )
            runCatching { profileRepository.getProfileRooms(userIdentifierPreferences.id) }
                .onFailure {
//                    toast(it.message.orEmpty())
                }
                .getOrDefault(emptyList())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val rooms = roomsDeffered.await()

            val roomWithRequests =
                rooms?.map {
                    async { Pair(it, roomRepository.roomRequests(it.name)) }
                }?.awaitAll()

            if (roomWithRequests != null) {
                Log.e("build model room", roomWithRequests.toString())
                buildModels(roomWithRequests)
            }
        }
    }

    private fun fetchRoomMessages() {
        Log.e(TAG, "Inside fetchRoomMessages()")
//        val messageDeffered = viewLifecycleOwner.lifecycleScope.async {
//            Log.e(TAG,"fetchRoomMessages: ${profileRepository.getMessageForRoom() }")
//
//            kotlin.runCatching { profileRepository.getMessageForRoom() }
//                .onFailure { it.printStackTrace() }
//                .getOrDefault(emptyList())
//        }
//        viewLifecycleOwner.lifecycleScope.launch {
//            val messages = messageDeffered.await()
//
//            buildMessageModel(messages)
//        }
    }


    private fun buildMessageModel(messages: List<CreateRoomRequest>) {
        binding.createRoomRequests.withModels {
            messages.forEach { createRoomRequest ->
                createRoomRequest {
                    id(createRoomRequest.hashCode())
                    userPicture(createRoomRequest.profileImage)
                    userName(createRoomRequest.userName)
                    message(createRoomRequest.message)
                    createRoomListener { _ ->
                        val createRoomFragment = CreateRoomFragment(this@ManageRoomFragment)
                        createRoomFragment.show(parentFragmentManager, createRoomFragment.tag)
                    }
                }
            }
        }
    }

//    private fun buildMessageListModel(messages: List<UserRoomMessageList>) {
//        binding.newepoxy.withModels {
//            messages.forEach { userRoomMessageList ->
//                userRoomMessageListForManageroom{
//                    id(userRoomMessageList.hashCode())
//                    chatUserName(userRoomMessageList.chatUserName)
//                    chatUserMessage(userRoomMessageList.chatUserMessage)
//                    chatUserProfilePicture(userRoomMessageList.chatUserProfilePicture)
//                    chatMessageDate(userRoomMessageList.chatMessageDate)
//                }
//            }
//        }
//    }


    private fun buildModels(roomWithRequests: List<Pair<Room, List<RequestProfile>>>) {
        buildRoomModels(roomWithRequests.map { it.first })
        buildRequestModels(roomWithRequests)
    }

    private fun buildRoomModels(rooms: List<Room>) {
        Log.e("buildModelRoomsFunc", rooms.toString())
        binding.rooms.withModels {
            rooms.forEach { room ->
                listItemRoom {
                    id(room.id)
                    Log.i("Room data", "$room")
                    onGoLiveClick { v ->

                        val intent: Intent = Intent();
                        intent.setClassName(
                            requireContext(),
                            "cessini.technology.commonui.activity.GridActivity"
                        )
                        startActivity(intent)

                        viewLifecycleOwner.lifecycleScope.launch {
                            runCatching { roomRepository.startRoom(room.name) }.onSuccess {
                                navigator.navigateToFlow(NavigationFlow.AccessRoomFlow(room.name))
                                tvHeading.visibility = View.GONE
                                tvdeatils.visibility = View.GONE
                                view3.visibility = View.VISIBLE
                                view4.visibility = View.VISIBLE
                                /////////////////////////////////////////////////////////
                                toast(message = "You are Live.")

                            }.onFailure {
                                toast(message = "Unable to go live.")
                                tvHeading.visibility = View.VISIBLE
                                tvdeatils.visibility = View.VISIBLE
                            }
                        }
                    }
                    onDetail { _ ->
                        navigator.navigateToFlow(NavigationFlow.AccessRoomFlow(room.name))

                    }
                    userPicture(room.creator.profilePicture)
                    roomName(room.title)
                    userName(room.time.dateTime())
                    onProfileClick { _ ->
                        navigateToProfile(
                            room.creator.id,
                            baseViewModel.id.value.orEmpty()
                        )
                    }

                }
            }
        }
    }

    private fun addOrRemoveUser(roomName: String, id: String) {
        val acceptedUsers = acceptedRequets[roomName].orEmpty().toMutableList()
            .apply { if (contains(id)) remove(id) else add(id) }

        acceptedRequets[roomName] = acceptedUsers

        runCatching { fetchRooms() }
    }

    private fun buildRequestModels(roomWithRequests: List<Pair<Room, List<RequestProfile>>>) {
        binding.requests.withModels {
            roomWithRequests
                .filter { it.second.isNotEmpty() }
                .onEach { (room, requests) ->
                    buildRequestSeperator(room.title, room.name)
                    buildRequests(room.title, requests)
                    tvHeading.visibility = View.GONE
                    tvdeatils.visibility = View.GONE
                    view3.visibility = View.VISIBLE
                    view4.visibility = View.VISIBLE
                }

        }


    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    private fun EpoxyController.buildRequestSeperator(roomName: String, roomCode: String) {
        listItemSepRequest {
            id(roomName.hashCode())
            title("${roomName.trimEnd()} requests")

            if (acceptedRequets[roomName].isNullOrEmpty()) {
                liveStatus(false)
            } else {
                liveStatus(true)
            }

            onClick { _ ->
                if (acceptedRequets[roomName].isNullOrEmpty()) {
                    toast(message = "Please accept one request.")
                    return@onClick
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    runCatching {
                        roomRepository.acceptRoomRequests(
                            roomCode,
                            acceptedRequets[roomName].orEmpty()
                        )
                    }.onSuccess {
                        acceptedRequets[roomName] = mutableListOf()

                        runCatching { fetchRooms() }

                        toast(message = "Request accepted.")
                    }.onFailure {
                        toast(message = it.message.orEmpty())
                    }
                }
            }
        }
    }

    private fun EpoxyController.buildRequests(roomName: String, requests: List<RequestProfile>) {
        requests.forEach {
            listItemRequest {
                id(it.id)
                reqUserImage(it.picture)
                reqUserChannel(it.channel)
                reqUserName(it.name)
                onReqProfileClick { _ ->
                    navigateToProfile(
                        it.id,
                        baseViewModel.id.value.orEmpty()
                    )
                }
                onAccept { _ -> addOrRemoveUser(roomName, it.id) }
            }
        }
    }


    override fun onSheet2Dismissed() {
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {
        binding.manageRoomScrollView.layoutParams.height = -1
        setLevel(0)
    }

    override fun getHeightOfBottomSheet(height: Int) {
        binding.manageRoomScrollView.layoutParams.height = height + 10.toPx().toInt()
        setLevel(-1)
    }

}
