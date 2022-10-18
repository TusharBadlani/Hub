package cessini.technology.cvo.exploremodels

data class SearchFriendsModel(
    val id: Int,
    val username: String,
    val email: String
) : Comparable<SearchFriendsModel> {
    override fun compareTo(other: SearchFriendsModel): Int {
        val compare: SearchFriendsModel = other

        return if (compare.username === this.username && compare.email == this.email && compare.id === this.id) {
            0
        } else 1
    }

}
