package cessini.technology.newapi.preferences

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthPreferences @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(auth_preferences, Context.MODE_PRIVATE)

    var accessToken: String
        get() = preferences.getString(access_token, "")!!
        set(value) {
            preferences.edit { putString(access_token, value) }
            accessTokenExpiry = System.currentTimeMillis() + one_day
        }

    var refreshToken: String
        get() = preferences.getString(refresh_token, "")!!
        set(value) {
            preferences.edit { putString(refresh_token, value) }
        }

    val loggedIn get() = accessToken.isNotEmpty()

    val tokenExpire: Boolean get() = System.currentTimeMillis() - accessTokenExpiry > one_day

    fun deleteAccessToken() {
        accessTokenExpiry = 0L
    }

    private var accessTokenExpiry: Long
        get() = preferences.getLong(access_token_expiry, 0L)
        set(value) {
            preferences.edit { putLong(access_token_expiry, value) }
        }

    companion object {
        private const val one_day = 86400L
        private const val access_token = "access_token"
        private const val refresh_token = "refresh_token"
        private const val access_token_expiry = "access_token_expiry"
        const val auth_preferences = "auth_preferences"
    }
}
