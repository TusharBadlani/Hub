package cessini.technology.commonui.common

import android.content.Context
import androidx.fragment.app.Fragment
import cessini.technology.commonui.R

fun Fragment.isInDarkTheme(): Boolean {
    return requireActivity().getSharedPreferences(
        getString(R.string.sharedPrefName),
        Context.MODE_PRIVATE
    ).getBoolean("darktheme", false)
}
