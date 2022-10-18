package cessini.technology.newapi.services.timeline_room

import cessini.technology.newapi.model.LiveRoomData
import cessini.technology.newapi.model.LiveRoomTimeline
import cessini.technology.newapi.model.RoomTimeline
import retrofit2.Call
import retrofit2.http.GET

interface LiveRoomService {
    @GET(RoomConstants.LIVE_ROOM)
    fun getLiveRooms(): Call<List<LiveRoomTimeline>>
}