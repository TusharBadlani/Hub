package cessini.technology.newapi.services.timeline_room

import cessini.technology.model.RoomViews
import cessini.technology.model.TotalRoomViewResponse
import cessini.technology.model.search.UserLikes
import cessini.technology.newapi.model.ApiMessage
import cessini.technology.newapi.model.RoomTimeline
import cessini.technology.newapi.services.myspace.MySpaceConstants
import cessini.technology.newapi.services.myspace.model.body.UserLikeBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RoomTimelineService {
    @GET(RoomConstants.ROOM_TIMELINE)
    fun getTimeLineRooms(): Call<RoomTimeline>

    @GET(RoomConstants.ROOM_TOTAL_ROOM_VIEWERS)
    fun getTotalRoomViewers(): Call<TotalRoomViewResponse>

    @GET(RoomConstants.ROOM_VIEWS)
    fun getRoomViews():Call<RoomViews>

    @GET(value = "${MySpaceConstants.GET_LIKES}{id}")
     fun getLikes(
        @Path(value = "id") id: String,
    ): Call<UserLikes>

    @POST(MySpaceConstants.LIKE_ROOM)
    suspend fun likeRoom(
        @Body userLikeBody: UserLikeBody,
    ): ApiMessage
}