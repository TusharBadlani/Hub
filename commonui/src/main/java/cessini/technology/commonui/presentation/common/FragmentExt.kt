package cessini.technology.commonui.presentation.common

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        requireContext(),
        message,
        length,
    ).show()
}

