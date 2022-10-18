package cessini.technology.newrepository.appRepository

import cessini.technology.newrepository.datastores.AppStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val appStore: AppStore
) {

    val isDarkThemeEnabled: Flow<Boolean> by appStore::isDarkThemeEnabled

    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        appStore.setDarkThemeEnabled(enabled)
    }

    val isBoardingFinished: Flow<Boolean> by appStore::isOnBoardingFinished

    suspend fun setOnBoardingFinished(isOnBoardingFinished: Boolean) {
        appStore.setIsOnBoardingFinished(isOnBoardingFinished)
    }
}