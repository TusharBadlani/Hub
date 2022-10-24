package cessini.technology.commonui.presentation.commonchat

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.commonui.databinding.FragmentCommonChatBinding
import cessini.technology.commonui.intro
import cessini.technology.commonui.item
import cessini.technology.commonui.utils.Constant
import cessini.technology.commonui.data.models.CommonChatPayload
import com.google.android.material.bottomsheet.BottomSheetDialog


class CommonChatFragment
    : BaseBottomSheet<FragmentCommonChatBinding>(R.layout.fragment_common_chat),
    BottomSheetLevelInterface {


    private val chatViewModel : CommonChatViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel.messages.observe(viewLifecycleOwner){
            populateMessages(it)
        }

        binding.ivSend.setOnClickListener {
            val etText = binding.etText.text
            if(etText.isNotEmpty()){
                //TODO UserID
                chatViewModel.emitMessage(
                    CommonChatPayload(
                        message = etText.toString(),
                        room = "TODO",
                        user_id = "Enquire"
                    )
                )
                etText.clear()
            }
        }

    }


    private fun populateMessages(messages: List<CommonChatPayload>){
        binding.rvChat.withModels {
            intro{}
            messages.forEachIndexed { index, commonChatPayload ->
                 //TODO Check if the message is sent by user
                 item { message(commonChatPayload) }

             }
        }
    }

    companion object {
        const val TAG = "CommonChatFragment"
    }




    override fun onSheet2Dismissed() {
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {
        setLevel(0)

    }

    override fun getHeightOfBottomSheet(height: Int) {
        setLevel(-1)
    }

}