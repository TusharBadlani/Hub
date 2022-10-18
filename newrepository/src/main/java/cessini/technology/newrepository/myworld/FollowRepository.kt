package cessini.technology.newrepository.myworld

import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.myworld.MyWorldService
import cessini.technology.newapi.services.myworld.model.body.UserIdBody
import cessini.technology.newapi.services.myworld.model.response.ApiFollowerFollowing
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import cessini.technology.model.*
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val myWorldApi: MyWorldService
    ) {
    suspend fun getFollowing(id: String): Flow<Resource<List<User>>>{
        return flow {

            try {
                val response =  myWorldApi.getFollowing(id).toModel()
                emit(Resource.Success(data = response))
            } catch (e: IOException) {
                e.printStackTrace()
                //emit custom error message here according to exception or error that may be displayed to the user
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }

        }
    }


    suspend fun getFollowers(id: String) =
        myWorldApi.getFollowers(id).getOrThrow().toModel()

    suspend fun followUser(id: String) {
        myWorldApi.followUser(UserIdBody(id)).getOrThrow()
    }

    suspend fun unFollowUser(id: String) {
        myWorldApi.unFollowUser(UserIdBody(id)).getOrThrow()
    }
}
