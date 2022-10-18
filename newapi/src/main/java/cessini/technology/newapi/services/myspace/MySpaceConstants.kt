package cessini.technology.newapi.services.myspace

object MySpaceConstants {
    // TODO: Replace with https://rooms-api.joinmyworld.live/
    const val BASE_ENDPOINT = "http://65.1.147.5/"
    const val CREATE_ROOM_ENDPOINT = "create_room/"
    const val GET_ROOM_ENDPOINT = "get_room/"
    //const val JOIN_ROOM_ENDPOINT = "add/"
    const val JOIN_ROOM_ENDPOINT = "addPublic/"
    const val ACCEPT_JOIN_REQUEST_ENDPOINT = "accept/"
    const val REQUEST_ROOM_ENDPOINT = "list/"
    const val REMOVE_LISTENER = "remove_listner/"
    const val REQUEST_LIVE_ROOMS = "live_rooms/"
    const val START_ROOM = "start_room/"
    const val DELETE_ROOM = "delete_room/"
    const val GET_LIKES = "user/room_likes/"
    const val LIKE_ROOM = "like/"
}
