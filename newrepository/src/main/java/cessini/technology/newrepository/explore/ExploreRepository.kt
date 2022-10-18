package cessini.technology.newrepository.explore

import android.util.Log
import androidx.paging.*
import cessini.technology.model.*
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.model.ApiNotification
import cessini.technology.newapi.model.ApiNotificationWhenLoggedIn
import cessini.technology.newapi.services.explore.ExploreInfoService
import cessini.technology.newapi.services.explore.ExploreRecordService
import cessini.technology.newapi.services.explore.ExploreService
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class ExploreRepository @Inject constructor(
    private val exploreService: ExploreService,
    private val userIdentifierPreferences: UserIdentifierPreferences,
    private val exploreInfoService: ExploreInfoService,
    private val exploreRecordService: ExploreRecordService,
) {



//    suspend fun getExploreVideos():ExploreVideosResponse{
//        return exploreService.getExploreVideos()
//    }

//    suspend fun getDetails(id:String): Response<Component> {
//        return exploreService.getDetails(userIdentifierPreferences.id)
//    }


    @ExperimentalPagingApi
    fun getLiveRoomsRegisterUser(): Flow<PagingData<LiveRoom>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                ExplorePagination(exploreService, true, userIdentifierPreferences)
            }
        ).flow
    }

    @ExperimentalPagingApi
    fun getLiveRoomsSignedUser(): Flow<PagingData<LiveRoom>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                ExplorePagination(
                    exploreService,
                    false,
                    userIdentifierPreferences
                )
            }
        ).flow
    }

    suspend fun exploreUser(): Flow<Resource<Explore>> {

        return flow {
            try {

                val response = if (userIdentifierPreferences.loggedIn) {
                    //If user is Signed In (get data for signed in user)
                    exploreService.explore(userIdentifierPreferences.id).toModel()
                } else {

                    //If user is not Signed In (get data for not signed in user)
                    exploreService.exploreUnAuth(userIdentifierPreferences.uuid).toModel()
                }

                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }
        }

    }


    suspend fun getsuggestedrooms(): Flow<Resource<suggestionroomresponse>> {
        Log.d("Hemlo", "get suggested rooms")
        Log.d("Hemlo", "${userIdentifierPreferences.id} ")

        return flow {

            try {

                val response = if (!userIdentifierPreferences.loggedIn) {
                    exploreService.getSuggestedRooms(userIdentifierPreferences.uuid)
                } else {
                    exploreService.getSuggestedRooms(userIdentifierPreferences.id)
                }

                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }

    }

    suspend fun getNotifications(): ApiNotificationWhenLoggedIn {
//        return exploreService.getNotification(TokenBody(token)).getOrThrow().data.map { it.toModel() }
        return exploreService.getNotification()
    }

    suspend fun getNotificationPlease(): Response<ApiNotification> {
        return exploreService.getNotificationPlease()
    }

    suspend fun componentSignedUser(): Flow<Resource<Component>> {
        Log.e("UserId", userIdentifierPreferences.id)
        return flow {
            try {
                val response = exploreService.getComponentAuth(userIdentifierPreferences.id)
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }
        }
    }

    suspend fun componentRegisterUser(): Flow<Resource<Component>> {

        return flow {


            try {
                val response = exploreService.getComponentUnAuth(userIdentifierPreferences.uuid)
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }
    }

    suspend fun getInfo(): Flow<Resource<Info>> {
        return flow {

            try {
                val response = exploreInfoService.getInfo()
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }

    }

    suspend fun getRecordedVideos(): Flow<Resource<RecordedVideo>> {

        return flow {

            try {
                val response = exploreRecordService.getRecordedVideos(userIdentifierPreferences.id)
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }

    }

    suspend fun getRecordedVideosProfile(id: String): Flow<Resource<RecordedVideo>> {

        return flow {


            try {
                val response = exploreRecordService.getRecordedVideos(id)
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }

    }

    suspend fun getCommonRecordedVideos(): Flow<Resource<RecordedVideo>> {
        return flow {

            try {
                val response = exploreRecordService.getCommonRecordedVideos()
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }
    }


    suspend fun getTrendingRooms(): Flow<Resource<TrendingRooms>> {

        return flow {
            try {
                val response = exploreService.getTrendingRooms()
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user or data ( dummy or empty data if required)
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }
    }
}
