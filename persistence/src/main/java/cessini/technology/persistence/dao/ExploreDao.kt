package cessini.technology.persistence.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cessini.technology.model.LiveRoom

@Dao
interface ExploreDao {

    @Insert
    suspend fun insertAllLiverRooms(liverRooms : List<LiveRoom>)

    @Query("DELETE FROM LiveRooms")
    suspend fun deleteAllLiveRooms()

    @Query("SELECT * FROM LiveRooms")
    fun getAllLiveRooms(): PagingSource<Int,LiveRoom>

    @Query("SELECT * FROM LiveRooms")
    fun getLiveRooms() : List<LiveRoom>
}