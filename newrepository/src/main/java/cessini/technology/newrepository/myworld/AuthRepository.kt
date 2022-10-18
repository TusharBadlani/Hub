package cessini.technology.newrepository.myworld

import android.util.Log
import cessini.technology.newapi.services.myworld.MyWorldService
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.myworld.model.response.ApiProfile
import cessini.technology.newapi.services.myworld.model.body.AuthBody
import cessini.technology.newapi.services.myworld.model.response.Token
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newapi.services.myworld.model.response.ApiProfileData
import cessini.technology.newrepository.datastores.ProfileStore
import cessini.technology.newrepository.explore.RegistrationRepository
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val myWorldApi: MyWorldService,
    private val authPreferences: AuthPreferences,
    private val profileStore: ProfileStore,
    private val registrationRepository: RegistrationRepository,
    private val userIdentifierPreferences: UserIdentifierPreferences,
) {
    suspend fun authenticate(googleIdToken: String): ApiProfileData {
        return myWorldApi.authenticate(AuthBody(googleIdToken)).getOrThrow().also {
            storeToken(it.data.token)

            userIdentifierPreferences.id = it.data.profile.id
            storeProfile(it.data.profile)

            runCatching { registrationRepository.registerNotification(userIdentifierPreferences.firebaseToken) }
        }.data
    }

    private fun storeToken(token: Token) {
        with(authPreferences) {
            accessToken = token.access
            refreshToken = token.refresh
        }
    }

    private suspend fun storeProfile(profile: ApiProfile) {
        profileStore.storeProfile(profile.toModel())
    }
}
