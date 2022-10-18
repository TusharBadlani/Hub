package cessini.technology.cvo.cameraModels

data class Audio(
    var id : Int,
    var category : String?,
    var uploads : ArrayList<SongInfo>
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