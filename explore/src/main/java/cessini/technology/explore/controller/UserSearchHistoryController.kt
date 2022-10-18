package cessini.technology.explore.controller

import android.content.Context
import com.airbnb.epoxy.AsyncEpoxyController
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.explore.fragment.ExploreSearchFragment
import cessini.technology.explore.userSearchHistory

class UserSearchHistoryController(var context: Context?, var parentFrag: ExploreSearchFragment?) :
    AsyncEpoxyController() {
    var historyList = ArrayList<SearchHistoryEntity>()
        set(value) {
            field = value
            requestModelBuild()
        }


    override fun buildModels() {
        historyList.forEachIndexed { index, searchFriendsModel ->

            userSearchHistory {
                id(index)
                historyModel(searchFriendsModel)

                onClickHistory { _, _, _, position ->
//                    Toast.makeText(
//                        context,
//                        "Query: ${historyList[position].query}",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    parentFrag!!.binding.searchViewHeader.setQuery(historyList[position].query, true)
                }
            }

        }
    }
}