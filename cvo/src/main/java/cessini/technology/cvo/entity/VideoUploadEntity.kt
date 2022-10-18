package cessini.technology.cvo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_upload_table")
data class VideoUploadEntity(
    @PrimaryKey var url: String,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "desc") var desc: String = ""
)
