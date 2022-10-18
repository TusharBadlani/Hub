package cessini.technology.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cessini.technology.cvo.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Insert
    suspend fun insertSearchQuery(searchHistoryEntity: SearchHistoryEntity)

    @Delete
    suspend fun deleteSearchQuery(searchHistoryEntity: SearchHistoryEntity?)

    @Query("DELETE FROM search_history_table")
    suspend fun nukeSearchHistoryTable()

    @Query("SELECT * FROM search_history_table")
    suspend fun getAllSearchQueries(): List<SearchHistoryEntity>
}