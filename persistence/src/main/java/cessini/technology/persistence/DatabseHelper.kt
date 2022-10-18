package cessini.technology.persistence

import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.cvo.entity.HomeFeedEntity
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.cvo.entity.VideoUploadEntity

interface DatabaseHelper {
    suspend fun insertAuth(authEntity: AuthEntity)
    suspend fun getAuthById(id: String): AuthEntity?
    suspend fun nukeAuthTable()
    suspend fun getAllAuth(): List<AuthEntity>
    suspend fun updateEmailById(email: String, id: String)
    suspend fun updateUsernameById(username: String, id: String)
    suspend fun updateProfilePictureById(profile_picture: String, id: String)
    suspend fun updateAccessTokenById(access_token: String, id: String)
    suspend fun updateRefreshTokenId(refresh_token: String, id: String)
    suspend fun updateBioById(bio: String, id: String)
    suspend fun updateIdTokenById(idToken: String, id: String)


    suspend fun insertVideo(videoEntity: VideoUploadEntity)
    suspend fun deleteVideo(videoEntity: VideoUploadEntity?)
    suspend fun nukeVideoTable()
    suspend fun getVideoByUrl(url: String): VideoUploadEntity?
    suspend fun getAllVideos(): List<VideoUploadEntity>?

    suspend fun insertSearchQuery(searchHistoryEntity: SearchHistoryEntity)
    suspend fun deleteSearchQuery(searchHistoryEntity: SearchHistoryEntity?)
    suspend fun nukeSearchHistoryTable()

    //    suspend fun getQueriesByKeywords(query: String): SearchHistoryEntity
    suspend fun getAllSearchQueries(): List<SearchHistoryEntity>?

    suspend fun insertHomeFeed(homeFeedEntity: HomeFeedEntity)
    suspend fun deleteHomeFeed(homeFeedEntity: HomeFeedEntity?)
    suspend fun nukeHomeFeedTable()
    suspend fun getAllHomeFeed(): List<HomeFeedEntity>
}