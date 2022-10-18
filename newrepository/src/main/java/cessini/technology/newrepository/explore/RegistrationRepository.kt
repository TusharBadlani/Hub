package cessini.technology.newrepository.explore

import android.util.Log
import cessini.technology.model.Category
import cessini.technology.newapi.services.explore.ExploreService
import cessini.technology.newapi.services.explore.model.body.RegisterAuthUserBody
import cessini.technology.newapi.services.explore.model.body.TokenBody
import cessini.technology.newapi.services.explore.model.body.UserRegistrationBody
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import javax.inject.Inject

class RegistrationRepository @Inject constructor(
    private val exploreService: ExploreService,
    private val userIdentifierPreferences: UserIdentifierPreferences,
) {
    suspend fun registerCategories(categories: Set<String>, sub_categories: Set<String>) {
        Log.e("RegisteringUser",userIdentifierPreferences.uuid)
        exploreService.registerCategory(
            UserRegistrationBody(userIdentifierPreferences.uuid, categories,sub_categories)
        )
    }

    suspend fun registerAuthUser() {
        exploreService.registerCategory(
            RegisterAuthUserBody(userIdentifierPreferences.uuid)
        )
    }

    suspend fun registerNotification(token: String) {
        exploreService.registerNotification(TokenBody(token))
    }

    suspend fun getVideoCategories() : Category {
        return exploreService.getVideoCategories()
    }

    suspend fun registerCategoriesAfterLogin(userId:String, categories: Set<String>, sub_categories: Set<String>){
        Log.e("RegisteringLoggedInUser",userId)
        exploreService.registerCategory(
            UserRegistrationBody(userId, categories,sub_categories)
        )
    }
}
