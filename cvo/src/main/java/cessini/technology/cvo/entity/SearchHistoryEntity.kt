package cessini.technology.cvo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history_table")
data class SearchHistoryEntity(
    @PrimaryKey var id: String = "",
    @ColumnInfo(name = "query") var query: String = "",
    @ColumnInfo(name = "category") var category: String = ""
)