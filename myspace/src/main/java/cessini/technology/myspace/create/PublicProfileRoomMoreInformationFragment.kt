package cessini.technology.myspace.create

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.myspace.R
import cessini.technology.myspace.databinding.FragmentPublicProfileRoomMoreInformationBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublicProfileRoomMoreInformationFragment(private val listener: BottomSheetLevelInterface) : BaseBottomSheet<FragmentPublicProfileRoomMoreInformationBinding>(
    R.layout.fragment_public_profile_room_more_information
) {

    private val shared_pref_name_for_know_more: String = "know_more"
    private val know_more_variable: String = "isKnowMoreOpened"

    private lateinit var preference: SharedPreferences

    private val publicProfileRoomMoreInformationFragmentViewModel : PublicProfileRoomMoreInformationFragmentViewModel by activityViewModels()

    var count = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight + 12)
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
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                publicProfileRoomMoreInformationFragmentViewModel.updateMoreInfoDraggedState(slideOffset)
            }
        })
        dialog.behavior.isDraggable = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener.onSheet2Created()
        preference = requireContext().getSharedPreferences(
            shared_pref_name_for_know_more,
            Context.MODE_PRIVATE
        )
        binding.btnNext.setOnClickListener {
            preference.edit { putBoolean(know_more_variable, true) }
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onSheet2Dismissed()
    }
}