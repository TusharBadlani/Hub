package cessini.technology.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.cvo.entity.HomeFeedEntity
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.cvo.entity.VideoUploadEntity
import cessini.technology.model.LiveRoom
import cessini.technology.persistence.dao.*

@Database(
    entities = [AuthEntity::class, VideoUploadEntity::class, SearchHistoryEntity::class, HomeFeedEntity::class,LiveRoom::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LiveCreatorTypeConverter::class,ListenerTypeConverter::class,StringTypeConverter::class)
abstract class MyWorldDB : RoomDatabase() {
    abstract fun authDao(): AuthDao
    abstract fun videoUploadDao(): VideoUploadDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun homeFeedDao(): HomeFeedDao
    abstract fun exploreDao() : ExploreDao
}