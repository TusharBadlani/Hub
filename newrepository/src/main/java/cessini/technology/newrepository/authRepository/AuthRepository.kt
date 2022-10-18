package cessini.technology.newrepository.authRepository

import android.util.Log
import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.newapi.RetrofitClient
import cessini.technology.persistence.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val dbHelper: DatabaseHelper
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

//    private val dbHelper: cessini.technology.persistence.DatabaseHelperImpl =
//        cessini.technology.persistence.DatabaseHelperImpl(
//            cessini.technology.persistence.DatabaseBuilder.getInstance(
//                application
//            )
//        )

//    suspend fun getAuthById(id: String) {
//        val authEntity: AuthEntity? = dbHelper.getAuthById(id) //need to return this later
//        Log.d("DB", "Entity fetched from DB is: ${authEntity.toString()}")
//    }

    /**Function to get all the user tuples from DB
     * We expect to receive just one tuple from DB*/
    suspend fun getAllAuth(): List<AuthEntity> {
        val authList: List<AuthEntity> = withContext(Dispatchers.IO) {
            dbHelper.getAllAuth() //need to return this later
        }

        Log.d("DB", "Entity fetched from DB is: $authList")
        if (authList.size > 1) {
            Log.d("DB", "Multiple entities exist in DB")
        } else {
            cessini.technology.newapi.RetrofitClient.authToken = authList[0].access_token
            Log.d("AuthRepository", "Access Token: ${RetrofitClient.authToken}")
        }

        return authList
    }

//    suspend fun updateAuth(authEntity: AuthEntity): AuthEntity {
//        withContext(IO) {
//            dbHelper.nukeAuthTable()
//            dbHelper.insertAuth(authEntity)
//        }
//        return authEntity
//    }

    /**Function to insert user data in DB when the user Signs In: */
    suspend fun insertAuth(authEntity: AuthEntity): AuthEntity {
        withContext(Dispatchers.IO) {
            val res = kotlin.runCatching {
                dbHelper.insertAuth(authEntity)
            }

            res.onSuccess {
                RetrofitClient.authToken = authEntity.access_token
                Log.d("AuthRepository", "Access Token: ${RetrofitClient.authToken}")
            }

        }

        return authEntity
    }


    suspend fun nukeAuthTable() {
        withContext(Dispatchers.IO) {
            val res = kotlin.runCatching {
                dbHelper.nukeAuthTable();
            }

            res.onSuccess {
                Log.d(TAG, "Nuked the auth table")
            }

            res.onFailure {
                Log.d(TAG, "Nuking the auth table failed")
            }
        }
    }





    fun clearRetrofitAuthToken() {
        cessini.technology.newapi.RetrofitClient.authToken = null
    }


//    suspend fun updateAccessTokenById(access_token: String, id: String) {
//        dbHelper.updateAccessTokenById(access_token, id)
//    }


}
