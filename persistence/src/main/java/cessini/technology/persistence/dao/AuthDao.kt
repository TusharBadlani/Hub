package cessini.technology.persistence.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import cessini.technology.cvo.entity.AuthEntity

@Dao
interface AuthDao {
    @Insert
    suspend fun insertDao(authEntity: AuthEntity)

    @Delete
    suspend fun deleteAuth(authEntity: AuthEntity?)

    @Query("DELETE FROM auth_table")
    suspend fun nukeAuthTable()

    @Query("SELECT * FROM auth_table WHERE id = :id")
    suspend fun getAuthById(id :String): AuthEntity?

    @Query("SELECT * FROM auth_table")
    suspend fun getAllAuth(): List<AuthEntity>

    @Query("UPDATE auth_table SET email = :email WHERE id = :id")
    suspend fun updateEmailById(email: String, id :String)

    @Query("UPDATE auth_table SET displayName = :displayName WHERE id = :id")
    suspend fun updateUsernameById(displayName: String, id :String)

    @Query("UPDATE auth_table SET photoUrl = :photoUrl WHERE id = :id")
    suspend fun updateProfilePictureById(photoUrl: String, id :String)

    @Query("UPDATE auth_table SET bio = :bio WHERE id = :id")
    suspend fun updateBioById(bio: String, id :String)

    @Query("UPDATE auth_table SET idToken = :idToken WHERE id = :id")
    suspend fun updateIdTokenById(idToken: String, id :String)

    @Query("UPDATE auth_table SET access_token = :access_token WHERE id = :id")
    suspend fun updateAccessTokenById(access_token: String, id :String)

    @Query("UPDATE auth_table SET refresh_token = :refresh_token WHERE id = :id")
    suspend fun updateRefreshTokenId(refresh_token: String, id :String)

}