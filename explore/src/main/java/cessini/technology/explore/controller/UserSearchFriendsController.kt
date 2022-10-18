package cessini.technology.explore.controller

import android.content.Context
import android.util.Log
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import com.airbnb.epoxy.AsyncEpoxyController
import cessini.technology.cvo.exploremodels.SearchFriendsModel
import cessini.technology.explore.userSearchFriendsItem
import java.util.*
import kotlin.collections.ArrayList

class UserSearchFriendsController(var context: Context) : AsyncEpoxyController(), Filterable {
    var allFriends: MutableList<SearchFriendsModel> =
        emptyList<SearchFriendsModel>().toMutableList()

    var friendsFilterList = ArrayList<SearchFriendsModel>()
        set(value) {
            field = value
            requestModelBuild()
        }


    override fun buildModels() {
        friendsFilterList.forEachIndexed { index, searchFriendsModel ->
            userSearchFriendsItem {
                id(index)
                friend(searchFriendsModel)
                onClickFriends { _, _, _, _ ->
                    Toast.makeText(context, "Item no: $index", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getFilter(): Filter {
        friendsFilterList = allFriends as ArrayList<SearchFriendsModel>
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    friendsFilterList = allFriends as ArrayList<SearchFriendsModel>
                } else {
                    val resultList = ArrayList<SearchFriendsModel>()
                    for (row in allFriends) {
                        val query1 = row.username
                        val query2 = row.email
                        if ((query1.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT))) || (query2.toLowerCase(
                                Locale.ROOT
                            ).contains(charSearch.toLowerCase(Locale.ROOT)))
                        ) {
                            resultList.add(row)
                        }
                    }
                    friendsFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = friendsFilterList
                Log.i("Friends Filter List", friendsFilterList.toString())
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                friendsFilterList = results?.values as ArrayList<SearchFriendsModel>
                Log.i("Friends Filter List", friendsFilterList.toString())
                requestModelBuild()
            }

        }
    }
}