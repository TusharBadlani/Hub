package cessini.technology.persistence

import cessini.technology.cvo.entity.AuthEntity
import cessini.technology.cvo.entity.HomeFeedEntity
import cessini.technology.cvo.entity.SearchHistoryEntity
import cessini.technology.cvo.entity.VideoUploadEntity
import javax.inject.Inject


class DatabaseHelperImpl @Inject constructor(
    private val myWorldDB: MyWorldDB
    ) : DatabaseHelper {
    override suspend fun insertAuth(authEntity: AuthEntity) =
        myWorldDB.authDao().insertDao(authEntity)

    override suspend fun getAuthById(id: String): AuthEntity? =
        myWorldDB.authDao().getAuthById(id)

    override suspend fun nukeAuthTable() {
        myWorldDB.authDao().nukeAuthTable()
    }

    override suspend fun getAllAuth(): List<AuthEntity> = myWorldDB.authDao().getAllAuth()
    override suspend fun updateEmailById(email: String, id: String) {
        myWorldDB.authDao().updateEmailById(email, id)
    }

    override suspend fun updateUsernameById(username: String, id: String) {
        myWorldDB.authDao().updateUsernameById(username, id)
    }

    override suspend fun updateProfilePictureById(profile_picture: String, id: String) {
        myWorldDB.authDao().updateProfilePictureById(profile_picture, id)
    }

    override suspend fun updateAccessTokenById(access_token: String, id: String) {
        myWorldDB.authDao().updateAccessTokenById(access_token, id)
    }

    override suspend fun updateRefreshTokenId(refresh_token: String, id: String) {
        myWorldDB.authDao().updateRefreshTokenId(refresh_token, id)
    }

    override suspend fun updateBioById(bio: String, id: String) {
        myWorldDB.authDao().updateBioById(bio, id)
    }

    override suspend fun updateIdTokenById(idToken: String, id: String) {
        myWorldDB.authDao().updateIdTokenById(idToken, id)
    }

    override suspend fun insertVideo(videoEntity: VideoUploadEntity) {
        myWorldDB.videoUploadDao().insertVideo(videoEntity)
    }

    override suspend fun deleteVideo(videoEntity: VideoUploadEntity?) {
        myWorldDB.videoUploadDao().deleteVideo(videoEntity)
    }

    override suspend fun nukeVideoTable() {
        myWorldDB.videoUploadDao().nukeVideoTable()
    }

    override suspend fun getVideoByUrl(url: String): VideoUploadEntity? =
        myWorldDB.videoUploadDao().getVideoByUrl(url)


    override suspend fun getAllVideos(): List<VideoUploadEntity>? =
        myWorldDB.videoUploadDao().getAllVideos()

    override suspend fun insertSearchQuery(searchHistoryEntity: SearchHistoryEntity) {
        myWorldDB.searchHistoryDao().insertSearchQuery(searchHistoryEntity)
    }

    override suspend fun deleteSearchQuery(searchHistoryEntity: SearchHistoryEntity?) {
        myWorldDB.searchHistoryDao().deleteSearchQuery(searchHistoryEntity)
    }

    override suspend fun nukeSearchHistoryTable() {
        myWorldDB.searchHistoryDao().nukeSearchHistoryTable()
    }

    override suspend fun getAllSearchQueries(): List<SearchHistoryEntity>? {
        return myWorldDB.searchHistoryDao().getAllSearchQueries()
    }

    override suspend fun insertHomeFeed(homeFeedEntity: HomeFeedEntity) {
        myWorldDB.homeFeedDao().insertHomeFeed(homeFeedEntity)
    }

    override suspend fun deleteHomeFeed(homeFeedEntity: HomeFeedEntity?) {
        myWorldDB.homeFeedDao().deleteHomeFeed(homeFeedEntity)
    }

    override suspend fun nukeHomeFeedTable() {
        myWorldDB.homeFeedDao().nukeHomeFeedTable()
    }

    override suspend fun getAllHomeFeed(): List<HomeFeedEntity> =
        myWorldDB.homeFeedDao().getAllHomeFeed()
}