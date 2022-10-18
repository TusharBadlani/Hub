package cessini.technology.newrepository.datastores

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import cessini.technology.model.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Boolean.FALSE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val Context.dataStore by preferencesDataStore(
        name = data_store
    )

    fun getProfile(): Flow<Profile> = context.dataStore.data.map {
        Profile(
            id = it[ProfileStoreKeys.id].orEmpty(),
            name = it[ProfileStoreKeys.name].orEmpty(),
            email = it[ProfileStoreKeys.email].orEmpty(),
            expertise = it[ProfileStoreKeys.expertise].orEmpty(),
            channelName = it[ProfileStoreKeys.channelName].orEmpty(),
            provider = it[ProfileStoreKeys.provider].orEmpty(),
            bio = it[ProfileStoreKeys.bio].orEmpty(),
            verified=it[ProfileStoreKeys.verified]?:false,
            location = it[ProfileStoreKeys.location].orEmpty(),
            profilePicture = it[ProfileStoreKeys.profilePicture].orEmpty(),
            followerCount = it[ProfileStoreKeys.followerCount] ?: 0,
            followingCount = it[ProfileStoreKeys.followingCount] ?: 0,
            following = false,
        )
    }

    suspend fun deleteProfile() {
        context.dataStore.edit { it[ProfileStoreKeys.id] = "" }
    }

    suspend fun storeProfile(profile: Profile) {
        context.dataStore.edit {
            it[ProfileStoreKeys.id] = profile.id
            it[ProfileStoreKeys.name] = profile.name
            it[ProfileStoreKeys.email] = profile.email
            it[ProfileStoreKeys.expertise] = profile.expertise
            it[ProfileStoreKeys.channelName] = profile.channelName
            it[ProfileStoreKeys.provider] = profile.provider
            it[ProfileStoreKeys.bio] = profile.bio
            it[ProfileStoreKeys.location] = profile.location
            it[ProfileStoreKeys.profilePicture] = profile.profilePicture
            it[ProfileStoreKeys.followerCount] = profile.followerCount
            it[ProfileStoreKeys.followingCount] = profile.followingCount
            it[ProfileStoreKeys.verified]=profile.verified
        }
    }

    companion object {
        private const val data_store = "profile_data_store"
    }
}
