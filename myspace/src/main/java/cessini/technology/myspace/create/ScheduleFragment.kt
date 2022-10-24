package cessini.technology.myspace.create

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.myspace.R
import cessini.technology.myspace.databinding.FragmentScheduleBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ScheduleFragment(val levelInterface: BottomSheetLevelInterface,) : BaseBottomSheet<FragmentScheduleBinding>(R.layout.fragment_schedule) {

    var count = 0

    private val roomSharedViewModel: CreateRoomSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        levelInterface.onSheet2Created()

        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
        with(binding) {
            backButton.setOnClickListener {
                navigateToRoom()
            }

            doneButton.setOnClickListener {
                roomSharedViewModel.time.value = datepicker.date.time
                navigateToRoom()
            }

            roomSharedViewModel.roomTitle.observe(viewLifecycleOwner) {
                roomTitle.text = it
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight + 12)
        }

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 == 0) {
                        dismiss()
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
        dialog.behavior.isDraggable = false
        return dialog
    }

    private fun navigateToRoom() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        levelInterface.onSheet2Dismissed()
    }
}
