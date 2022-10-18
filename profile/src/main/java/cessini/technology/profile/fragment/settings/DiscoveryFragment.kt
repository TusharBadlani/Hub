package cessini.technology.profile.fragment.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.common.BottomSheetLevelInterface
import cessini.technology.profile.R
import cessini.technology.profile.databinding.FragmentDiscoveryBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoveryFragment(private val listener: BottomSheetLevelInterface) : BaseBottomSheet<FragmentDiscoveryBinding>(R.layout.fragment_discovery) {

    var count = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        returnHeightWhenReady()

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }

        binding.switch1.setOnClickListener {
            binding.switch1.isChecked = false
            Toast.makeText(
                requireContext(),
                "This feature need 100 watch hours to active",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switch2.setOnClickListener {
            binding.switch2.isChecked = false
            Toast.makeText(
                requireContext(),
                "This feature need 100 watch hours to active",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.switch3.setOnClickListener {
            binding.switch3.isChecked = false
            Toast.makeText(
                requireContext(),
                "This feature need 100 watch hours to active",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight + 6)
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
        binding.discoveryConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.discoveryConstraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                listener.getHeightOfBottomSheet(binding.discoveryConstraintLayout.height)
            }

        })
    }

    override fun onResume() {
        super.onResume()

        binding.backNavigationButtonDiscoveryProfile.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onSheet1Dismissed()
    }

}