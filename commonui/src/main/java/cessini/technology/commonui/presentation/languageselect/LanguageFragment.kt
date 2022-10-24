package cessini.technology.commonui.presentation.languageselect

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.databinding.FragmentLanguageBinding
import cessini.technology.navigation.MainNavGraphDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageFragment : BaseFragment<FragmentLanguageBinding>(R.layout.fragment_language) {

    companion object {
        const val TAG = "LanguageFragment"
    }

    private val viewModel: LanguageSelectionFragmentViewModel by viewModels()

    // TODO: Extend to other supported languages
    private val localeChipMap = mapOf(
        R.id.english_language to "en",
        R.id.hindi_language to "hi",
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener { onClickNext() }

        Log.e("CheckNav","LanguageFragment, ${activity?.localClassName}")
    }

    private fun onClickNext() {
        binding.progressBar.isVisible = true
        val locale = localeChipMap[binding.chipGroup.checkedChipId]

        if (locale != null) {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(locale)
            )
        }

        try {
            viewModel.submitOnBoardingSelection()
            findNavController().navigate(MainNavGraphDirections.actionGlobalHomeFlow())
        } catch (e: Exception) {
            Log.e(TAG, "Unable to submit on-boarding selection", e)
            Toast.makeText(
                requireContext(),
                getString(R.string.error_on_boarding_submission),
                Toast.LENGTH_SHORT
            )
        } finally {
            binding.progressBar.isGone = true
        }
    }
}