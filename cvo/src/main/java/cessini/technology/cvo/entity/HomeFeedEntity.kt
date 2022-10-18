package cessini.technology.cvo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "homeFeed_table")
data class HomeFeedEntity(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "video_id") var video_id: Int,
    @ColumnInfo(name = "video_length") var video_length: String,
    @ColumnInfo(name = "thumbnail") var thumbnail: String,
    @ColumnInfo(name = "chunk1") var chunk1: String,
    @ColumnInfo(name = "chunk2") var chunk2: String,
    @ColumnInfo(name = "chunk3") var chunk3: String,
    @ColumnInfo(name = "chunk4") var chunk4: String,
    @ColumnInfo(name = "video_title") var video_title: String = "Nature",
    @ColumnInfo(name = "video_description") var video_description: String = "Nature is beautiful"
)
