package cessini.technology.newrepository.datastores

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object ProfileStoreKeys {
    val id = stringPreferencesKey(name = "profile_id")
    val name = stringPreferencesKey(name = "profile_name")
    val email = stringPreferencesKey(name = "profile_email")
    val expertise = stringPreferencesKey(name = "area_of_expert")
    val profilePicture = stringPreferencesKey(name = "profile_picture")
    val channelName = stringPreferencesKey(name = "profile_channel_name")
    val bio = stringPreferencesKey(name = "profile_bio")
    val verified= booleanPreferencesKey(name="verified")
    val provider = stringPreferencesKey(name = "profile_provider")
    val location = stringPreferencesKey(name = "profile_location")
    val followerCount = intPreferencesKey(name = "profile_follower")
    val followingCount = intPreferencesKey(name = "profile_following")
}
