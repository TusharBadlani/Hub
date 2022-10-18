package cessini.technology.newrepository.datastores

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data store for basic and global app preferences
 */
@Singleton
class AppStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private object Keys {
        val darkThemeEnabled = booleanPreferencesKey(name="dark_theme_enabled")
        val isOnBoardingFinished = booleanPreferencesKey(name="on_boarding_finished")
    }

    private val Context.dataStore by preferencesDataStore(
        name = "app_preferences"
    )

    val isDarkThemeEnabled: Flow<Boolean> get() =
        context.dataStore.data.map {
            it[Keys.darkThemeEnabled] ?: false
        }

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit {
            it[Keys.darkThemeEnabled] = enabled
        }
    }

    val isOnBoardingFinished: Flow<Boolean> = context.dataStore.data.map {
        it[Keys.isOnBoardingFinished] ?: false
    }

    suspend fun setIsOnBoardingFinished(isOnBoardingFinished: Boolean) {
        context.dataStore.edit {
            it[Keys.isOnBoardingFinished] = isOnBoardingFinished
        }
    }
}