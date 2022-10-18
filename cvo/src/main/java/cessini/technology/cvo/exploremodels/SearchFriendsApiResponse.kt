package cessini.technology.cvo.exploremodels

data class SearchFriendsApiResponse(
    val count: Int,
    val next: Any?,
    val previous: Any?,
    val results: ArrayList<SearchFriendsModel>
)
