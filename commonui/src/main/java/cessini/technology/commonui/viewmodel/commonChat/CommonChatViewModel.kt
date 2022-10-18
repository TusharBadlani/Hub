package cessini.technology.commonui.viewmodel.commonChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cessini.technology.newapi.services.commonChat.CommonChatSocketHandler
import io.socket.client.Socket

class CommonChatViewModel : ViewModel() {

    private val _messages = MutableLiveData<List<CommonChatPayload>>()
    val messages: LiveData<List<CommonChatPayload>> get() = _messages
    private lateinit var mSocket: Socket

    fun setSocket(roomID: String){
        CommonChatSocketHandler.setSocket(roomID)
        if(!CommonChatSocketHandler.getSocket().connected()){
            CommonChatSocketHandler.establishConnection()
        }
        mSocket = CommonChatSocketHandler.getSocket()
    }



    fun listenTo(roomID: String){
        mSocket.on("message"){
            //TODO Update LiveData
        }
    }

    fun emitMessage(message: CommonChatPayload){
        mSocket.emit("message")
        //TODO Emit
    }


    companion object{
        const val TAG = "CommonChatVM"
    }


}