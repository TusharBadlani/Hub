package cessini.technology.newrepository.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maintains a preference of user's selected subcategory IDs as a set of strings
 */
@Singleton
class VideoCategoryStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val Context.datastore by preferencesDataStore(
        name = "video_category"
    )

    private object Keys {
        val subcategories = stringSetPreferencesKey(name = "subcategories")
    }

    val subCategoryIds: Flow<Set<String>>
        get() = context.datastore.data.map { preferences ->
            preferences[Keys.subcategories] ?: emptySet()
        }

    // Categories are derived from sub category IDs
    // This assumed that sub-category IDs are always in format categoryId_subcategoryId
    val categoryIds = subCategoryIds.map { subCategories ->
        subCategories.mapNotNull { subCategoryId ->
            subCategoryId.split("_").firstOrNull()
        }.toSet()
    }

    suspend fun setSubCategoryIds(subCategories: Set<String>) {
        context.datastore.edit { mutablePreferences ->
            mutablePreferences[Keys.subcategories] = subCategories
        }
    }
}