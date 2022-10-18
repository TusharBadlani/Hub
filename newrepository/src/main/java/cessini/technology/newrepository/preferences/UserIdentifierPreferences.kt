package cessini.technology.newrepository.preferences

import android.content.Context
import androidx.core.content.edit
import cessini.technology.newapi.preferences.AuthPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject

class UserIdentifierPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authPreferences: AuthPreferences,
) {
    private val preferences = context.getSharedPreferences(
        user_identifier_preferences,
        Context.MODE_PRIVATE,
    )

    val loggedIn: Boolean get() = authPreferences.accessToken.isNotEmpty()

    var id: String
        get() = preferences.getString(user_id, "")!!
        internal set(value) {
            preferences.edit { putString(user_id, value) }
        }

    var firebaseToken: String
        get() = preferences.getString(firebase_token, "")!!
        set(value) {
            preferences.edit {
                putString(firebase_token, value)
            }
        }

    var uuid: String
        get() = preferences.getString(user_identifier, "")!!.ifEmpty {
            UUID.randomUUID().toString().also {
                preferences.edit { putString(user_identifier, it) }
            }
        }
        set(value) {
            preferences.edit {
                putString(user_identifier, value)
            }
        }

    companion object {
        private const val user_id = "user_id"
        private const val firebase_token = "firebase_token"
        private const val user_identifier = "user_identifier"
        private const val user_identifier_preferences = "user_identifier_preferences"
    }
}
