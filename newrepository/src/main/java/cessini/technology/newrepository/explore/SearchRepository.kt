package cessini.technology.newrepository.explore


import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.persistence.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val dbHelper: DatabaseHelper

) {
//    private val dbHelper: DatabaseHelperImpl =
//        DatabaseHelperImpl(DatabaseBuilder.getInstance(application))

    suspend fun insertSearchQueryInDB(searchHistoryEntity: SearchHistoryEntity) {
        withContext(Dispatchers.IO) { dbHelper.insertSearchQuery(searchHistoryEntity) }
    }

    suspend fun getAllSearchQueryInDB(): List<SearchHistoryEntity>? {
        return withContext(Dispatchers.IO) { dbHelper.getAllSearchQueries() }
    }
}
