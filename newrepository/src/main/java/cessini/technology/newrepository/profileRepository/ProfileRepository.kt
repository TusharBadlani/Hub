package cessini.technology.newrepository.profileRepository


import android.util.Log
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.persistence.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val dbHelper: DatabaseHelper
) {
    companion object {
        private const val TAG = "ProfileRepository"
    }

    lateinit var displayName: String
    var id: String = ""
//    private val dbHelper: DatabaseHelperImpl =
//        DatabaseHelperImpl(DatabaseBuilder.getInstance(application))

    suspend fun getUser(): AuthEntity? {
        val authList: List<AuthEntity> = withContext(Dispatchers.IO) { dbHelper.getAllAuth() }

        Log.d("DB", "Entity fetched from DB is: $authList")
        if (authList.isEmpty() || authList.size > 1) {
            Log.d("DB", "Multiple entities exist in DB")
            dbHelper.nukeAuthTable()
            return null
        }

        displayName = authList[0].displayName
        id = authList[0].id

        Log.d("DB", "Returning User ${authList[0]} to ProfileViewModel")
        return authList[0]
    }


    suspend fun updateDB(authEntity: AuthEntity): AuthEntity {
        dbHelper.nukeAuthTable()

        withContext(Dispatchers.IO) {
            dbHelper.insertAuth(authEntity)
        }

        return withContext(Dispatchers.IO) {
            dbHelper.getAllAuth()[0]
        }

    }

}
