package cessini.technology.myspace.access

import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.myspace.R
import cessini.technology.myspace.databinding.FragmentBottomSheetAcessRoomBinding


class BottomSheetAcessRoomFragment :   BaseBottomSheet<FragmentBottomSheetAcessRoomBinding>(R.layout.fragment_bottom_sheet_acess_room),
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
