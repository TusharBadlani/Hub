package cessini.technology.newrepository.timlineRoomRepo

import cessini.technology.model.RoomViews
import cessini.technology.model.TotalRoomViewResponse
import cessini.technology.model.search.UserLikes
import cessini.technology.newapi.model.LiveRoomTimeline
import cessini.technology.newapi.model.RoomTimeline
import cessini.technology.newapi.services.myspace.model.body.UserLikeBody
import cessini.technology.newapi.services.timeline_room.LiveRoomService
import cessini.technology.newapi.services.timeline_room.RoomTimelineService
import retrofit2.Response
import retrofit2.awaitResponse
import javax.inject.Inject

class TimelineRepository @Inject constructor(
    private val liveRoomApi: LiveRoomService,
    private val roomTimeLineApi: RoomTimelineService
) {
    var apiRoomTimelineService = roomTimeLineApi

    suspend fun getTimeLineRooms(): Response<RoomTimeline> {
        return apiRoomTimelineService.getTimeLineRooms().awaitResponse()
    }

    suspend fun getTotalRoomViewers(): Response<TotalRoomViewResponse>{
        return apiRoomTimelineService.getTotalRoomViewers().awaitResponse()
    }

    suspend fun getRoomViews():Response<RoomViews>{
        return apiRoomTimelineService.getRoomViews().awaitResponse()
    }

    suspend fun getLikes(id: String):Response<UserLikes>{
        return apiRoomTimelineService.getLikes(id).awaitResponse()
    }

    suspend fun likeRoom(roomName: String, userId:String) {
        apiRoomTimelineService.likeRoom(UserLikeBody(userId,roomName))
    }
}

class LiveRoomRepository @Inject constructor(
    private val liveRoomApi: LiveRoomService,
    private val roomTimeLineApi: RoomTimelineService
) {
    var apiLiveRoom= liveRoomApi

    suspend fun getLiveRoom():Response<List<LiveRoomTimeline>>
    {
        return apiLiveRoom.getLiveRooms().awaitResponse()
    }
}