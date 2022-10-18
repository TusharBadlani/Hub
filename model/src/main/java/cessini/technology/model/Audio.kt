package cessini.technology.model

data class Audio(
    var id: Int,
    var category: String?,
    var uploads: ArrayList<SongInfo>
) : Comparable<Any?>,
    Cloneable {

    public override fun clone(): Audio {
        return try {
            super.clone() as Audio
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }

    override fun compareTo(other: Any?): Int {
        val compare = other as Audio
        return if (compare.category == category && compare.uploads == uploads && compare.id == id) {
            0
        } else 1
    }
}

data class SongInfo(
    var id: String,
    var title: String?,
    var thumbnail: String?,
    var duration: Int,
    var upload_file: String?

) : Comparable<Any?>,
    Cloneable {

    public override fun clone(): SongInfo {
        return try {
            super.clone() as SongInfo
        } catch (e: CloneNotSupportedException) {
            throw RuntimeException(e)
        }
    }

    override fun compareTo(other: Any?): Int {
        val compare = other as SongInfo
        return if (compare.upload_file == upload_file && compare.title == title && compare.duration == duration) {
            0
        } else 1
    }
}
