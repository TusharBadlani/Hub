package cessini.technology.profile.activity

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.Navigator
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.profile.*
import cessini.technology.profile.`class`.*
import cessini.technology.profile.chatSocket.SocketHandler
import cessini.technology.profile.databinding.FragmentPublicprofileroommessageBinding
import cessini.technology.profile.fragment.publicProfile.SimpleModel
import cessini.technology.profile.viewmodel.ProfileViewModel
import cessini.technology.profile.viewmodel.PublicProfileViewModel
import com.google.gson.GsonBuilder
import com.vanniktech.emoji.EmojiButton
import com.vanniktech.emoji.EmojiEditText
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_publicprofileroommessage.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class PublicProfileRoomMessage : Fragment() {
    companion object {
        private const val TAG = "PublicProfileRoomMessage"
    }

    private val pArgs: PublicProfileRoomMessageArgs by navArgs()
    private val allMessages = ArrayList<SimpleModel>()
    private lateinit var binding: FragmentPublicprofileroommessageBinding

    private lateinit var viewModel: PublicProfileViewModel
    private lateinit var profileViewModel: ProfileViewModel

    var count = 0

    lateinit var mSocket: Socket

    lateinit var chatUserMe: JSONObject
    lateinit var chatUserId: JSONObject

    var userMe = ""
    var idMe = ""
    var idOther = ""

    private lateinit var baseViewModel: BaseViewModel

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    @Inject
    lateinit var navigator: Navigator
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = activity?.run {
            ViewModelProvider(this)[PublicProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.activity=requireActivity()
        profileViewModel = activity?.run {
            ViewModelProvider(this)[ProfileViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        baseViewModel = activity?.run {
            ViewModelProvider(this)[BaseViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_publicprofileroommessage,
            container,
            false
        )
        binding.lifecycleOwner = this
//        verifyLogin()
        viewModel.loadProfile(false)
        binding.recyclerGchat.withModels {
            itemChatHeader {
                id("chatHeader")
                Log.d(TAG,"This function is called and the username is ${viewModel.displayName.value}")
                userName("Welcome")
            }
        }
        binding.recyclerGchat.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("DERE", "onScrollStateChanged: "+getCurrentItem())
                    val position: Int = getCurrentItem()
                    setDateLabel(allMessages[position])
                }
            }
        })
        val rootView = binding.root
        val emojiPopup= EmojiPopup( rootView,binding.editGchatMessage)
        binding.emojiButton.setOnClickListener {
            if (emojiPopup.isShowing){
                emojiPopup.toggle()
            }
            else{
                emojiPopup.show()
            }
        }
        binding.editGchatMessage.setOnClickListener {
            if (emojiPopup.isShowing){
                emojiPopup.toggle()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.chatAction.setOnClickListener {
            findNavController().navigate(
                PublicProfileRoomMessageDirections.actionPublicProfileRoomMessageToChatBottomSheet()
            )
        }

        Log.d(TAG,"onViewCreate created")
        if(viewModel.displayName.value!=null)
            binding.userName = viewModel.displayName.value
        else
            binding.userName=""
        if(viewModel.photoUrl.value!=null)
            binding.profileImage = viewModel.photoUrl.value
        else
            binding.profileImage=""
//        binding.headText.setText(binding.userName)
        Log.d(TAG,"This function is called and the username is ${viewModel.displayName.value}")
        viewModel.photoUrl.observe(viewLifecycleOwner, Observer {
            Log.e("MessageFragment","ObserverCalled")
            binding.userName = viewModel.displayName.value
            binding.profileImage = viewModel.photoUrl.value
//            binding.headText.setText(binding.userName)
        })
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        Log.d(TAG,"The status of the socket is ${mSocket.isActive}")



        //other user
        idOther = pArgs.profileId




        // me user
        idMe = userIdentifierPreferences.id
        userMe = baseViewModel.authEntity.channelName


        chatUserMe = ChatUser(idMe, userMe, userMe).getJSONuser()



        back_navigation_button_discovery_profile.setOnClickListener {
//            findNavController().navigate(PublicProfileRoomMessageDirections.actionPublicProfileRoomMessageToPublicProfileFragment())
            findNavController().navigateUp()
        }

        binding.editGchatMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                verifyLogin()

                if (s.toString().trim().isNotEmpty()) {
                    binding.sendMessage.visibility = View.VISIBLE
                } else {
                    binding.sendMessage.visibility = View.INVISIBLE
                }
            }


            override fun afterTextChanged(s: Editable?) {}
        })

        binding.sendMessage.setOnClickListener {
            sendMessage()
        }

        binding.buttonSendEmoji.setOnClickListener {
            sendMessage()
        }


        mSocket.on("connect") {
            Log.d(TAG, "connected")
            mSocket.emit("send-info", chatUserMe)

            mSocket.emit("get-messages", getMessage(idMe, idOther).getMessages())
        }


        mSocket.on("get-messages-list-response") {
            Log.d(TAG, "get-messages-list-response ${it[0]}")
        }
        mSocket.on("send-info-status") { it ->
            Log.d(TAG, "send info status ${it[0]}")
        }

        try {
            val onUpdateChat = Emitter.Listener {
//                Log.d(TAG,"inside emitter : ${it[0]}")
//                Log.d(TAG, "get-messages-response ${it[0]}")
//
                val gson = GsonBuilder().create()
                val responseJson =
                    gson.fromJson(it[0].toString(), Array<ResponseJson>::class.java).toList()
//                Log.d(TAG, "response json: $responseJson")

                if (responseJson.size > 0) {
                    Log.d(TAG, "last element: ${responseJson[responseJson.size - 1].message}")
                    if (count == 0) {
                        responseJson.forEach {
//                        Log.d(TAG, "Inside responseJSon: ${it}")
                            if (it.sender == true) {
//                            simpleModels.add(SimpleModel(idMe, it.message))
                                allMessages.add(SimpleModel(idMe, it.message))
                            } else if (it.sender == false) {
//                            simpleModels.add(SimpleModel(idOther, it.message))
                                allMessages.add(SimpleModel(idOther, it.message))
                            }
                        }
                        activity?.runOnUiThread {
                            populateList(allMessages, idMe, idOther)
                            binding.recyclerGchat.smoothScrollToPosition(allMessages.size - 1)
//                            allMessages.removeAll(allMessages)
                        }
                        count = 1
                    } else {
                        if (responseJson[responseJson.size - 1].with == idOther) {
                            allMessages.add(
                                SimpleModel(
                                    idMe,
                                    responseJson[responseJson.size - 1].message
                                )
                            )
                            Log.d(TAG, "true== ${responseJson[responseJson.size - 1]}")
                        } else {
                            allMessages.add(
                                SimpleModel(
                                    idOther,
                                    responseJson[responseJson.size - 1].message
                                )
                            )
                            Log.d(TAG, "false== ${responseJson[responseJson.size - 1]}")

                        }
                        activity?.runOnUiThread {
                            populateList(allMessages, idMe, idOther)
//                            allMessages.removeAll(allMessages)
                        }
                    }


                }
//                simpleModels.removeAll(simpleModels)
            }
            mSocket.on("get-messages-response", onUpdateChat)
//            mSocket.on("get-messages-response",onUpdateChat) { it ->
//
//
//            }


        } catch (e: Exception) {
            Log.d(TAG, "can't reach get-messages-response $e")
        }

        var onSendMsg = Emitter.Listener {
            Log.d(TAG, "private-message ${it[0]}")

            val res: JSONObject = JSONObject(it[0].toString())
            res.optString("message")

            Log.d(TAG, "message received: ${res.optString("message")}")


            allMessages.add(SimpleModel(idOther, res.optString("message")))
            populateList(allMessages,idMe,idOther)
        }
        mSocket.on("private-message-response", onSendMsg)


//            mSocket.on("private-message-response",onSendMsg)
    }

    private fun getCurrentItem(): Int {
        return (binding.recyclerGchat.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
    }
    private fun setDateLabel(item: SimpleModel) {
        //TODO: change value to item.timestamp when timestamp field is added to simplemodel
        val dd = convertDateFormat("yyyy-MM-dd HH:mm:ss", "yyyy MMM dd", "10:23")
        binding.dateLabel.text = dd
        binding.dateLabel.visibility = View.VISIBLE


        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                binding.dateLabel.visibility = View.INVISIBLE
            }, 3000)
        }

    }
    fun convertDateFormat(FormFormat: String, ToFormat: String, value: String): String {
        var returnValue = value
        val formFormat = SimpleDateFormat(FormFormat, Locale.getDefault())
        val toFormat = SimpleDateFormat(ToFormat, Locale.getDefault())
        returnValue = formFormat.parse(value)?.let { toFormat.format(it) }.toString()
        return returnValue
    }
    fun sendMessage(){
        var firstClick = true
        if (firstClick) {
            firstClick = false
        }


        if (mSocket.connected()) {
            Log.d("socket", "already connected ")

            lifecycleScope.launch {
                var sendMsgText = binding.editGchatMessage.text.toString().trim()
                binding.editGchatMessage.setText("")


                mSocket.emit("private-message", message(idMe, idOther, sendMsgText).getMessage())
                count++
                mSocket.emit("get-messages", getMessage(idMe, idOther).getMessages())

                allMessages.add(SimpleModel(idMe, sendMsgText))
            }
            activity?.runOnUiThread {
                populateList(allMessages, idMe, idOther)
            }
            allMessages.removeLast()

        } else {
            Toast.makeText(activity, "Error sending message", Toast.LENGTH_LONG).show()
            Log.d(TAG, "not connected ")
        }
    }

    private fun populateList(simpleModels: MutableList<SimpleModel>, me: String, other: String) {
        Log.d(TAG, "populateList called")
        Log.d(TAG,"simple models: $simpleModels")
        binding.recyclerGchat.withModels {

            itemChatHeader {
                id("chatHeader")
//                TODO("Add chat header username here")
                userName("Welcome")
            }

            if (simpleModels != null) {

                simpleModels.forEachIndexed { position, model ->
                    when (model.type) {
                        me -> itemChatMe {
                            id(position)
                            sent(model.content)
                        }
                        other -> itemChatOther {
                            id(position)
                            recieved(model)
                            sentPhoto(viewModel.photoUrl.value)
                        }


                    }
                }
            }
        }
        recycler_gchat.smoothScrollToPosition(simpleModels.size)
    }

    private fun verifyLogin() {
        if (!userIdentifierPreferences.loggedIn) {
            navigator.navigateToFlow(NavigationFlow.AuthFlow)
            return
        }
    }
}