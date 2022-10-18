package cessini.technology.newrepository.mappers

import cessini.technology.model.Notification
import cessini.technology.newapi.model.ApiNotification

//internal fun ApiNotification.toModel() = Notification(
//    type = event.toNotificationType(),
//    id = id.orEmpty(),
//    message = message,
//    image = image,
////    time = (time.toFloatOrNull() ?: 0).toLong(),
//    time = time.toLongOrNull() ?: 0,
//    seen = seen,
//)

//internal fun String.toNotificationType(): Notification.Type {
//    return when (this) {
//        "video_like" -> Notification.Type.VIDEO_LIKE
//        "story_like" -> Notification.Type.STORY_LIKE
//        "video_comment" -> Notification.Type.VIDEO_COMMENT
//        "story_comment" -> Notification.Type.STORY_COMMENT
//        "Video" -> Notification.Type.VIDEO_UPLOAD
//        "Follow" -> Notification.Type.FOLLOW
//        "Room Request" -> Notification.Type.ROOM_JOIN_REQUEST
//        "room_join_request_accepted" -> Notification.Type.ROOM_JOIN_REQUEST_ACCEPTED
//        "create_room_request" -> Notification.Type.CREATE_ROOM_REQUEST
//        else -> Notification.Type.ERROR
//    }
//}
