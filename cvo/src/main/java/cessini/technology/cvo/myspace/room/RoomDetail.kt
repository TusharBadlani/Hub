package cessini.technology.cvo.myspace.room

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomDetail(
    val id: String,
    val name: String,
    val creator: String,
    val title: String,
    val time: Long,
    val private: Boolean,
) : Parcelable
