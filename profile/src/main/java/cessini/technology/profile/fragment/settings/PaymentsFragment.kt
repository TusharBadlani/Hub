package cessini.technology.profile.fragment.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.utils.Constant
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.profile.R
import cessini.technology.profile.databinding.FragmentPaymentsBinding
import cessini.technology.profile.viewmodel.ProfileViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentsFragment(private val listener: BottomSheetLevelInterface): BaseBottomSheet<FragmentPaymentsBinding>(
    R.layout.fragment_payments) {

    var count = 0
    private val profileViewModel: ProfileViewModel by viewModels()
    private var userUpiId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        returnHeightWhenReady()

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            isDraggable = false
        }
        setUpTextChangeListener()
        setUpSaveButton()
        setUpObserver()
    }

    private fun setUpTextChangeListener() = binding.userUpiId.addTextChangedListener{
        binding.userUpiId.error = null
    }

    private fun getCreatorUPIData() {
        profileViewModel.getCreatorUPIData(profileViewModel.userId)
    }

    private fun setUpObserver() {
        profileViewModel.upiSaveProgress.observe(viewLifecycleOwner) {
            when(it) {
                0 -> {
                    toggleProgressBar(true)
                }
                100 -> {
                    toggleProgressBar(false)
                    dismiss()
                }
                -1 -> {
                    toggleProgressBar(false)
                    binding.userUpiId.error = "Enter a valid UPI ID"
                }
                -2 -> {
                    toggleProgressBar(false)
                }
            }
        }
        profileViewModel.upiData.observe(viewLifecycleOwner) {
            val array = it.split("^")
            if(array.size >= 2) {
                binding.userUpiId.setText(array[0])
                binding.userUpiName.setText(array[1])
                userUpiId = array[0]
            }
        }
    }

    private fun toggleProgressBar(isVisible: Boolean) {
        binding.saveButton.isVisible = !isVisible
        binding.saveProgressBar.isVisible = isVisible
    }

    private fun setUpSaveButton() {
        binding.saveButton.setOnClickListener {
            val currentUpiId = binding.userUpiId.text.toString()
            if(userUpiId != currentUpiId) {
                profileViewModel.saveUPIData(currentUpiId, binding.userUpiName.text.toString(), userUpiId == "")
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        getCreatorUPIData()
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight + 6)
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
        binding.paymentUserCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                binding.paymentUserCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
                listener.getHeightOfBottomSheet(binding.paymentUserCl.height)
            }

        })
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