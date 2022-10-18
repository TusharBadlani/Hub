package cessini.technology.newapi.services.myspace

import cessini.technology.model.search.UserLikes
import cessini.technology.newapi.ApiParameters
import cessini.technology.newapi.interceptors.AuthInterceptor
import cessini.technology.newapi.model.ApiMessage
import cessini.technology.newapi.services.explore.model.body.TokenBody
import cessini.technology.newapi.services.myspace.model.body.AcceptRoomRequestBody
import cessini.technology.newapi.services.myspace.model.body.RoomBody
import cessini.technology.newapi.services.myspace.model.body.RoomNameBody
import cessini.technology.newapi.services.myspace.model.body.UserLikeBody
import cessini.technology.newapi.services.myspace.model.response.ApiGetRoom
import cessini.technology.newapi.services.myspace.model.response.ApiRoomName
import cessini.technology.newapi.services.myspace.model.response.ApiRoomRequests
import retrofit2.Response
import retrofit2.http.*

interface MySpaceService {
    @POST(MySpaceConstants.CREATE_ROOM_ENDPOINT)
    suspend fun createRoom(
        @Body roomBody: RoomBody,
    ): Response<ApiRoomName>

    @Headers(ApiParameters.NO_AUTH_HEADER)
    @GET(value = "${MySpaceConstants.GET_ROOM_ENDPOINT}{name}")
    suspend fun getRoom(
        @Path(value = "name") name: String,
    ): Response<ApiGetRoom>

    @GET(value = "${MySpaceConstants.JOIN_ROOM_ENDPOINT}{roomNameBody}")
    suspend fun joinRoom(
        @Path(value = "roomNameBody") roomNameBody: String,
    ): ApiMessage

//    @Headers(ApiParameters.AUTH_HEADER)
//    @POST(MySpaceConstants.JOIN_ROOM_ENDPOINT)
//    suspend fun joinRoom(
//        @Body roomNameBody: RoomNameBody,
//    ): ApiMessage

    @GET(value = "${MySpaceConstants.REQUEST_ROOM_ENDPOINT}{name}")
    suspend fun roomRequest(
        @Path(value = "name") name: String,
    ): Response<ApiRoomRequests>

    @POST(MySpaceConstants.ACCEPT_JOIN_REQUEST_ENDPOINT)
    suspend fun acceptRequest(
        @Body acceptRoomRequestBody: AcceptRoomRequestBody,
    ): ApiMessage

    @GET(value = "${MySpaceConstants.REMOVE_LISTENER}{room_code}/{profile_id}")
    suspend fun removeListener(
        @Path(value = "room_code") roomCode: String,
        @Path(value = "profile_id") profileID: String
    ) : Response<Unit>

    @GET(value = "${MySpaceConstants.REQUEST_LIVE_ROOMS}")
    suspend fun getLiveRooms(): Response<List<cessini.technology.model.LiveRoom>?>

    @GET(value = "${MySpaceConstants.START_ROOM}{room_code}")
    suspend fun startRoom(
        @Path(value = "room_code") roomCode: String,
    ): Response<Unit>

    @GET(value = "${MySpaceConstants.DELETE_ROOM}{room_code}")
    suspend fun deleteRoom(
        @Path(value = "room_code") roomCode: String,
    ): Response<Unit>

    @GET(value = "${MySpaceConstants.GET_LIKES}{id}")
    suspend fun getLikes(
        @Path(value = "id") id: String,
    ): UserLikes

    @POST(MySpaceConstants.LIKE_ROOM)
    suspend fun likeRoom(
        @Body userLikeBody: UserLikeBody,
    ): ApiMessage


}
