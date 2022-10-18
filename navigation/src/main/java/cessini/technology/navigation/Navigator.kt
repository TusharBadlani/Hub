package cessini.technology.navigation

import android.app.Activity
import androidx.navigation.NavController
import cessini.technology.navigation.NavigationFlow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigator @Inject constructor() {
    lateinit var navController: NavController
    lateinit var activity: Activity

    fun navigateToFlow(navigationFlow: NavigationFlow) {
        activity.runOnUiThread {
            with(navController) {
                when (navigationFlow) {
                    NoInternetFlow -> navigate(MainNavGraphDirections.actionGlobalNoInternetFlow())
                    CreateRoomFlow -> navigate(MainNavGraphDirections.actionGlobalCreateRoomFlow())
                    HomeFlow -> navigate(MainNavGraphDirections.actionGlobalHomeFlow())
                    ExploreFlow -> navigate(MainNavGraphDirections.actionGlobalExploreFlow())
                    CameraFlow -> navigate(MainNavGraphDirections.actionGlobalCameraFlow())
                    NotificationFlow -> navigate(MainNavGraphDirections.actionGlobalNotificationFlow())
                    ProfileFlow -> navigate(MainNavGraphDirections.actionGlobalProfileFlow())
                    GlobalProfileFlow -> navigate(MainNavGraphDirections.actionGlobalGlobalProfileFlow())
//                    GlobalMessageFlow -> navigate(MainNavGraphDirections.actionGlobalGlobalMessageFlow())
                    AuthFlow -> navigate(MainNavGraphDirections.actionGlobalAuthFlow())
                    SuggestionFlow -> navigate(MainNavGraphDirections.actionGlobalSuggestionFlow())
                    GlobalVideoFlow -> navigate(MainNavGraphDirections.actionGlobalVideoFlow())
                    GlobalStoryDisplayFlow -> navigate(MainNavGraphDirections.actionGlobalGlobalStoryDisplayFlow())
                    GlobalVideoDisplayFlow -> navigate(MainNavGraphDirections.actionGlobalGlobalVideoDisplayFlow())
                    GlobalEditProfileFlow -> navigate(MainNavGraphDirections.actionGlobalEditProfileFlow())
                    is AccessRoomFlow -> navigate(
                        MainNavGraphDirections.actionGlobalAccessRoom(
                            navigationFlow.roomName
                        )
                    )
                    is RoomsListFlow -> navigate(
                        MainNavGraphDirections.actionGlobalRoomsList(
                            navigationFlow.profileId
                        )
                    )
                    is GlobalMessageFlow -> navigate(
                        MainNavGraphDirections.actionGlobalGlobalMessageFlow(
                            navigationFlow.profileId,
                            navigationFlow.profileName
                        )
                    )
                }
            }
        }
    }
}