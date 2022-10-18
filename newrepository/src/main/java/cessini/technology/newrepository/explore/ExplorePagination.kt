package cessini.technology.newrepository.explore

import androidx.paging.*
import cessini.technology.model.LiveRoom
import cessini.technology.newapi.services.explore.ExploreService
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.persistence.MyWorldDB
import retrofit2.HttpException
import java.io.IOException

private val STARTING_PAGE_INDEX = 1

@ExperimentalPagingApi
class ExplorePagination constructor(
    private val exploreService: ExploreService,
    private val authRegisterUser: Boolean,
    private val userIdentifierPreferences: UserIdentifierPreferences
) : PagingSource<Int, LiveRoom>() {


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveRoom> {
        return try {
                if (authRegisterUser) {
                    val response = exploreService.exploreUnAuth(userIdentifierPreferences.uuid).toModel().liveRooms
                    LoadResult.Page(
                        data = response,
                        prevKey = null,
                        nextKey = null
                    )
                }else{
                    val response = exploreService.explore(userIdentifierPreferences.uuid).toModel().liveRooms
                    //val response = exploreService.explore("asdf").toModel().liveRooms
                    LoadResult.Page(
                        data = response,
                        prevKey = null,
                        nextKey = null
                    )
                }

        }catch (e : IOException){
            LoadResult.Error(e)
        }catch (e:HttpException){
            LoadResult.Error(e)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, LiveRoom>): Int? {
        TODO("Not yet implemented")
    }


}

//db.withTransaction {
//    if (loadType == LoadType.REFRESH){
//        db.exploreDao().deleteAllLiveRooms()
//    }
//
//    if (authRegisterUser) {
//        val response = exploreService.exploreUnAuth(userIdentifierPreferences.uuid).toModel().liveRooms
//        db.exploreDao().insertAllLiverRooms(response)
//    }else{
//        val response = exploreService.explore(userIdentifierPreferences.uuid).toModel().liveRooms
//        db.exploreDao().insertAllLiverRooms(response)
//    }
//}