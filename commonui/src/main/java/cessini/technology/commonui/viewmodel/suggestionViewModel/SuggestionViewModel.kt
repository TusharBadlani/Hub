package cessini.technology.commonui.viewmodel.suggestionViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cessini.technology.model.Subcategory
import cessini.technology.newrepository.explore.RegistrationRepository
import cessini.technology.newrepository.explore.VideoCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val registrationRepository: RegistrationRepository,
    private val videoCategoryRepository: VideoCategoryRepository,
) : ViewModel() {

    companion object {
        const val TAG = "SuggestionViewModel"
    }

    private val _categories = flow {
        val response = registrationRepository.getVideoCategories()
        emit(response.data.mapValues { it.value.toSet() })
    }.catch {
        Log.e(TAG, "Unable to fetch video categories", it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val categories: StateFlow<Map<String, Set<Subcategory>>?> = _categories

    private val _selectedSubCategories = MutableStateFlow<Set<Subcategory>>(emptySet())
    val selectedSubCategories: StateFlow<Set<Subcategory>> = _selectedSubCategories

    fun addToSelection(subcategory: Subcategory) {
        _selectedSubCategories.value = _selectedSubCategories.value + subcategory
    }

    fun removeFromSelection(subcategory: Subcategory) {
        _selectedSubCategories.value = _selectedSubCategories.value - subcategory
    }

    /**
     * Mark current selection as final
     */
    suspend fun finalizeCategories() {
        val selectedCategoryIds = selectedSubCategories.value
            .asIterable()
            .map { it.categoryID }
            .toSet()

        val selectedSubCategoryIds = selectedSubCategories.value
            .asIterable()
            .map { it.id }
            .toSet()

        videoCategoryRepository.setUserSelectedSubcategoryIds(selectedSubCategoryIds)

        registrationRepository.registerCategories(
            categories = selectedCategoryIds,
            sub_categories = selectedSubCategoryIds,
        )
    }
}