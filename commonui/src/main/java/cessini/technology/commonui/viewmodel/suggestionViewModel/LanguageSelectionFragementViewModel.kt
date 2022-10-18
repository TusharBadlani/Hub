package cessini.technology.commonui.viewmodel.suggestionViewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import cessini.technology.newrepository.appRepository.AppRepository
import cessini.technology.newrepository.explore.VideoCategoryRepository
import cessini.technology.newrepository.myworld.OnBoardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LanguageSelectionFragmentViewModel @Inject constructor(
    private val onBoardingRepository: OnBoardingRepository,
    private val categoryRepository: VideoCategoryRepository,
    private val appRepository: AppRepository,
): ViewModel() {

    /**
     * Submit on-boarding data to backend including language and category selection
     * and mark on boarding as finished, so on-boarding screen is not shown on next startup
     */
    suspend fun submitOnBoardingSelection() {
        val language = AppCompatDelegate.getApplicationLocales()[0]?.language
            ?: Locale.getDefault().language // fallback to default
            ?: "en" // fallback to english

        val selectedSubCategories = categoryRepository.subCategoryIds.first().toList()

        onBoardingRepository.submitOnBoardingSelection(
            language = language,
            subcategories = selectedSubCategories,
        )

        appRepository.setOnBoardingFinished(true)
    }
}