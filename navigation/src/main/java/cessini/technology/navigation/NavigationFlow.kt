package cessini.technology.navigation

sealed class NavigationFlow {
    class AccessRoomFlow(val roomName: String) : NavigationFlow()
    class RoomsListFlow(val profileId: String) : NavigationFlow()
    class GlobalMessageFlow(val profileId: String, val profileName: String): NavigationFlow()
    object NoInternetFlow : NavigationFlow()
    object CreateRoomFlow : NavigationFlow()
    object HomeFlow : NavigationFlow()
    object ExploreFlow : NavigationFlow()
    object CameraFlow : NavigationFlow()
    object NotificationFlow : NavigationFlow()
    object ProfileFlow : NavigationFlow()
    object GlobalProfileFlow : NavigationFlow()
    object AuthFlow : NavigationFlow()
    object SuggestionFlow : NavigationFlow()
    object GlobalVideoFlow : NavigationFlow()
    object GlobalStoryDisplayFlow : NavigationFlow()
    object GlobalVideoDisplayFlow : NavigationFlow()
    object GlobalEditProfileFlow : NavigationFlow()
}