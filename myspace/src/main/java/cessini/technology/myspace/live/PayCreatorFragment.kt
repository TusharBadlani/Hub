package cessini.technology.myspace.live

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.common.BaseBottomSheet
import cessini.technology.commonui.common.toast
import cessini.technology.commonui.databinding.VideoCategoryChipBinding
import cessini.technology.model.Room
import cessini.technology.myspace.R
import cessini.technology.myspace.databinding.FragmentPayCreatorBinding
import com.google.android.material.chip.Chip

class PayCreatorFragment(private val room: Room, private val upiId: String, private val upiName: String): BaseBottomSheet<FragmentPayCreatorBinding>(R.layout.fragment_pay_creator) {

    private var amount: Int = 100
    private val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.room = room
        binding.btnNext.text = getString(R.string.payment_button_value, room.creator.name, (binding.paymentChipGroup.getChildAt(0) as Chip).text)
        binding.paymentChipGroup.setOnCheckedChangeListener { group, checkedId ->
            for(i in 0 until binding.paymentChipGroup.childCount) {
                val chip = binding.paymentChipGroup.getChildAt(i) as Chip
                if(chip.isChecked) {
                    amount = if(chip.text == "Other..."){
                        100
                    } else {
                        chip.text.substring(1).toInt()
                    }
                    binding.btnNext.text = getString(R.string.payment_button_value, room.creator.name, chip.text)
                }
            }
        }
        binding.btnNext.setOnClickListener {
            val uri = getUpiPaymentUri()
            payWithGooglePay(uri)
        }
    }

    private fun payWithGooglePay(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        intent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
        (activity as HomeActivity).startActivityForResult(intent, 0)
    }

    private fun getUpiPaymentUri(): Uri {
        return Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa",upiId)
            .appendQueryParameter("pn",upiName)
            .appendQueryParameter("tn","Donate")
            .appendQueryParameter("am", amount.toString())
            .appendQueryParameter("cu","INR")
            .build()
    }

}