package cessini.technology.newrepository.explore

import cessini.technology.newrepository.preferences.VideoCategoryStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoCategoryRepository @Inject constructor(
    private val videoCategoryStore: VideoCategoryStore,
) {

    val subCategoryIds by videoCategoryStore::subCategoryIds

    val categoryIds by videoCategoryStore::categoryIds

    suspend fun setUserSelectedSubcategoryIds(subcategories: Set<String>) =
        videoCategoryStore.setSubCategoryIds(subcategories)
}
