package cessini.technology.explore.states

import cessini.technology.model.Room

sealed class ExploreOnClickEvents() {
    data class ToAccessRoomFlow(val room: Room) : ExploreOnClickEvents()
    data class ExploreFragmentToLiveFragment(val type: String, val type1: String) : ExploreOnClickEvents()
    object ToGlobalProfileFlow : ExploreOnClickEvents()
    object ShareEvent : ExploreOnClickEvents()
    object RefreshFeed : ExploreOnClickEvents()
}