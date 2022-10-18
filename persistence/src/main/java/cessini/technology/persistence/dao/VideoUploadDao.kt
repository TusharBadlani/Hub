package cessini.technology.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cessini.technology.cvo.entity.VideoUploadEntity

@Dao
interface VideoUploadDao {
    @Insert
    suspend fun insertVideo(videoEntity: VideoUploadEntity)

    @Delete
    suspend fun deleteVideo(videoEntity: VideoUploadEntity?)

    @Query("DELETE FROM video_upload_table")
    suspend fun nukeVideoTable()

    @Query("SELECT * FROM video_upload_table WHERE url = :url")
    suspend fun getVideoByUrl(url: String): VideoUploadEntity?

    @Query("SELECT * FROM video_upload_table")
    suspend fun getAllVideos(): List<VideoUploadEntity>
}