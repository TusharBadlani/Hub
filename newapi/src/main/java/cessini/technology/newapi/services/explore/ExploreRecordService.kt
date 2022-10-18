package cessini.technology.newapi.services.explore

import cessini.technology.model.RecordedVideo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExploreRecordService {
    @POST(ExploreConstants.RECORD_END_POINT)
    suspend fun getRecordedVideos(
        @Query("userid") id: String
    ): RecordedVideo

    @GET(ExploreConstants.COMMON_RECORD_END_POINT)
    suspend fun getCommonRecordedVideos(
    ): RecordedVideo
}