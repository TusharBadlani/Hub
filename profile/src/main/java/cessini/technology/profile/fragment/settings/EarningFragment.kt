package cessini.technology.profile.fragment.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.profile.R
import cessini.technology.profile.databinding.FragmentEarningBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EarningFragment(private val listener: BottomSheetLevelInterface) : BaseBottomSheet<FragmentEarningBinding>(R.layout.fragment_earning) {

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        returnHeightWhenReady()

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }

        binding.info.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                tooltip()
            }
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog,settingBottomSheetHeight + 6)
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
        return dialog
    }
    private fun returnHeightWhenReady() {
        binding.earningConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                binding.earningConstraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                listener.getHeightOfBottomSheet(binding.earningConstraintLayout.height)
            }

        })
    }

    private suspend fun tooltip() {
        binding.toolTip.animate().alpha(1f).duration = 500
        delay(4000)
        binding.toolTip.animate().alpha(0f).duration = 500
    }

    override fun onResume() {
        super.onResume()
        binding.backNavigationButtonEarningProfile.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onSheet1Dismissed()
    }

}