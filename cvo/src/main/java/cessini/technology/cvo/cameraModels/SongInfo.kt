package cessini.technology.cvo.cameraModels


data class SongInfo(
    var id : Int,
    var title : String?,
    var thumbnail : String?,
    var duration : Int,
    var upload_file : String?

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