package cessini.technology.profile.activity

import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.profile.R
import cessini.technology.profile.databinding.BottomSheetChatBinding

class ChatBottomSheet :   BaseBottomSheet<BottomSheetChatBinding>(R.layout.bottom_sheet_chat),
    BottomSheetLevelInterface {



    override fun onSheet2Dismissed() {
        setLevel(-1)
    }

    override fun onSheet2Created() {
        setLevel(-2)
    }

    override fun onSheet1Dismissed() {
        binding.lytBottomSheetChat.layoutParams.height = -1
        setLevel(0)
    }

    override fun getHeightOfBottomSheet(height: Int) {
        binding.lytBottomSheetChat.layoutParams.height = height + 10.toPx().toInt()
        setLevel(-1)
    }
}