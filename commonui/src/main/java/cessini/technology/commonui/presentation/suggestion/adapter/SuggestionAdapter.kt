package cessini.technology.commonui.presentation.suggestion.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import cessini.technology.commonui.presentation.appintro.IntroductionFragment
import cessini.technology.commonui.presentation.languageselect.LanguageFragment
import cessini.technology.commonui.presentation.suggestion.fragment.SuggestionFragment

class SuggestionAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    companion object {
        private const val ITEM_COUNT = 3
    }

    override fun getItemCount() = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        Log.i("Suggestion adaptor no.:", "$position")
        return when (position) {
            1 -> SuggestionFragment()
            2 -> LanguageFragment()
            else -> IntroductionFragment()
        }
    }
}