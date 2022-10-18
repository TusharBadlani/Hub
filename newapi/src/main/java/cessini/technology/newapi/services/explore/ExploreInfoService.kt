package cessini.technology.newapi.services.explore

import cessini.technology.model.Info
import retrofit2.Response
import retrofit2.http.GET

interface ExploreInfoService {

    @GET(ExploreConstants.INFO_END_POINT)
    suspend fun getInfo(): Info
}