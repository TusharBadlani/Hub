package cessini.technology.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.Navigator
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.notifications.controller.UserSearchController
import cessini.technology.notifications.databinding.FragmentMessageTabBinding
import cessini.technology.notifications.epoxyChat.ChatId
import cessini.technology.notifications.epoxyChat.ChatUser
import cessini.technology.notifications.epoxyChat.ResponseJson
import com.google.gson.GsonBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import io.socket.emitter.Emitter
import javax.inject.Inject

@AndroidEntryPoint
class MessageTabFragment : Fragment(), UserSearchController.AdapterCallbacks {
    companion object {
        private const val TAG = "MessageTabFragment"
    }

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var authPreferences: AuthPreferences

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    private val viewModel: ExploreSearchViewModel by activityViewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentMessageTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var controller: UserSearchController

    private lateinit var mSocket: Socket
    lateinit var idMe: String
    lateinit var userMe: String
    private var first_time = true
    private var prev: Int? = null
    private var listResponseJson = ArrayList<MessagesList>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_message_tab,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setCreatorRecycler() {
        binding.recyclerMessageSearch.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            controller = UserSearchController(this@MessageTabFragment)
            binding.recyclerMessageSearch.setController(controller)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        show(true)
        setCreatorRecycler()

        viewModel.creatorResponseModels.observe(viewLifecycleOwner) { list ->
            if (list.isNullOrEmpty()) {
                controller.allCreatorsExceptCurrentUser = emptyList()
                return@observe
            }
            controller.allCreatorsExceptCurrentUser =
                list.filter {
                    it.id != baseViewModel.id.value
                }
        }

        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()
        Log.d(TAG, "The status of connection is ${mSocket.isActive}")

        idMe = baseViewModel.authEntity.id
        userMe = baseViewModel.authEntity.channelName

        val chatUserMe = ChatUser(idMe, userMe, userMe).getJSONuser()


        mSocket.on("connect") {
            Log.d(TAG, "connected")
            mSocket.emit("send-info", chatUserMe)

            mSocket.emit("get-messages-list", ChatId(idMe).getJSONuser())

        }
        mSocket.on("get-messages-list-response") {
            Log.d(TAG, "get-messages-list-response ${it[0]}")
        }
        mSocket.on("send-info-status") {
            Log.d(TAG, "send info status ${it[0]}")
        }


        try {
            val onUpdateChat = Emitter.Listener {

                val gson = GsonBuilder().create()
                val responseJson =
                    gson.fromJson(it[0].toString(), Array<ResponseJson>::class.java).toList()
                Log.d(TAG, "responseJson: ${responseJson.isNotEmpty()}")
                if (responseJson.isNotEmpty()) {
                    if (first_time) {
                        Log.d(TAG, "last element: ${responseJson[responseJson.size - 1]}")
                        responseJson.forEach { response ->
                            listResponseJson.add(
                                MessagesList(
                                    response.with,
                                    response.username,
                                    response.profile_picture,
                                    response.message,
                                    response.time,
                                    response.read,
                                    response.sender
                                )
                            )
                        }
                        prev = responseJson.size
                        first_time = false
                    } else if (responseJson.size > prev!!) {
                        prev = responseJson.size
                        val response = responseJson[responseJson.size - 1]
                        listResponseJson.add(
                            MessagesList(
                                response.with,
                                response.username,
                                response.profile_picture,
                                response.message, response.time,
                                response.read,
                                response.sender
                            )
                        )
                    }
                }
//                if (responseJson.isNotEmpty() && first_time) {
//                    Log.d(TAG, "last element: ${responseJson[responseJson.size - 1]}")
//                    responseJson.forEach {
//                        listResponseJson.add(
//                            MessagesList(
//                                it.with,
//                                it.username,
//                                it.profile_picture,
//                                it.message,
//                                it.time,
//                                it.read,
//                                it.sender
//                            )
//                        )
//                    }
//                    prev=responseJson.size
//                    first_time=false
//                }
//                else if(responseJson.size> prev!!) {
//                    prev=responseJson.size
//                    val response=responseJson[responseJson.size-1]
//                    listResponseJson.add(MessagesList(response.with,
//                    response.username,
//                    response.profile_picture,
//                    response.message,response.time,
//                    response.read,
//                    response.sender))
//                }
                activity?.runOnUiThread {
                    populateMessage(listResponseJson)
                }
            }
            mSocket.on("get-messages-list-response", onUpdateChat)


        } catch (e: Exception) {
            Log.d(TAG, "can't reach get-messages-response $e")
        }

        binding.msgsearchViewHeader.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                Log.i("Submit searched query: ", query!!)
                viewModel.fetchSearchCreatorQueryAPI(query)
                viewModel.creatorResponseModels.observe(viewLifecycleOwner, Observer {


                    if (it != null) {
                        if (it.size > 0) {
                            show(false)
                            binding.recyclerMessageSearch.visibility = View.VISIBLE
                        } else if (it.size == 0) {
                            show(true)
                            binding.recyclerMessageSearch.visibility = View.GONE
                        }
                    }


                    //binding.recyclerViewCreatorHistory.visibility = View.GONE

                })

                if (query == "") {
                    show(true)
                    binding.recyclerMessageSearch.visibility = View.GONE
                    //binding.recyclerViewCreatorHistory.visibility = View.VISIBLE
                }

                //Insert to db
//                val entity = SearchHistoryEntity(
//                    id = "$query Creator",
//                    category = "Creator",
//                    query = query
//                )
//                viewModel.insertSearchQueryToDB(entity)


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                Log.e("Creator text pattern: ", newText!!)

                viewModel.creatorResponseModels.observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        if (it.size > 0) {
                            show(false)
                            binding.recyclerMessageSearch.visibility = View.VISIBLE
                        } else if (it.size == 0) {
                            binding.recyclerMessageSearch.visibility = View.GONE
                            show(true)
                            //Log.e("Creator text pattern: ", "Hide called")
                        }
                    } else {
                        show(true)
                        binding.recyclerMessageSearch.visibility = View.GONE
                    }
                })

                if (newText.isEmpty() || newText.length == 0) {
                    show(true)
                    binding.recyclerMessageSearch.visibility = View.GONE
                }

                if (newText.length == 2 || newText.length > 2) {
                    viewModel.fetchSearchCreatorQueryAPI(newText)
                    //binding.recyclerViewCreatorHistory.visibility = View.GONE
                    //binding.recyclerMessageSearch.visibility = View.VISIBLE
                }


                return false
            }

        })

    }

    private fun verifyLogin() {
        if (!userIdentifierPreferences.loggedIn) {
            navigator.navigateToFlow(NavigationFlow.AuthFlow)
            return
        }
    }

    var flag = true
    private fun populateMessage(list: MutableList<MessagesList>) {
        Log.d(TAG, "list: $list")
        binding.messageTabEpoxy.withModels {
            list.forEachIndexed { index, responseJson ->
                listItemMessage {
                    id(index)
                    chatUserName(responseJson.username)
                    chatUserProfilePicture(responseJson.profile_picture)
                    chatUserMessage(responseJson.message)
                    chatUserClickListener { _ ->
                        (activity as ToFlowNavigable).navigateToFlow(
                            NavigationFlow.GlobalMessageFlow(
                                responseJson.with,
                                responseJson.username
                            )
                        )
                    }
                    chatMessageDate(responseJson.time.toString())
                }

            }
        }
        show(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun show(flag: Boolean) {
        if (flag) {
            if (listResponseJson.size > 0) {
                binding.messageTabEpoxy.visibility = View.VISIBLE
                binding.emptyMessage.visibility = View.GONE
            } else {
                binding.messageTabEpoxy.visibility = View.GONE
                binding.emptyMessage.visibility = View.VISIBLE
            }
        } else {
            binding.messageTabEpoxy.visibility = View.GONE
            binding.emptyMessage.visibility = View.GONE
        }
    }

    override fun onUserSearchItemClicked(id: String, channelName: String) {
        (requireActivity() as ToFlowNavigable).navigateToFlow(
            NavigationFlow.GlobalMessageFlow(
                id, channelName
            )
        )
    }


}