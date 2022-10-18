package cessini.technology.cvo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "auth_table")
data class AuthEntity(

    @PrimaryKey var id: String,
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "expertise") var expertise: String = "",
    @ColumnInfo(name = "displayName") var displayName: String = "",
    @ColumnInfo(name = "channelName") var channelName: String = "",
    @ColumnInfo(name = "photoUrl") var photoUrl: String = "",
    @ColumnInfo(name = "idToken") var idToken: String = "",
    @ColumnInfo(name = "bio") var bio: String = "",
    @ColumnInfo(name = "access_token") var access_token: String = "",
    @ColumnInfo(name = "refresh_token") var refresh_token: String = "",
    @ColumnInfo(name = "location") var location: String= ""
)