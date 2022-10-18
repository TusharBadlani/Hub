package cessini.technology.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cessini.technology.cvo.entity.HomeFeedEntity

@Dao
interface HomeFeedDao {
    @Insert
    suspend fun insertHomeFeed(homeFeedEntity: HomeFeedEntity)

    @Delete
    suspend fun deleteHomeFeed(homeFeedEntity: HomeFeedEntity?)

    @Query("DELETE FROM homeFeed_table")
    suspend fun nukeHomeFeedTable()

    @Query("SELECT * FROM homeFeed_table")
    suspend fun getAllHomeFeed(): List<HomeFeedEntity>
}