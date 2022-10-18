package cessini.technology.home.epoxy

import cessini.technology.home.model.JoinRoomSocketEventPayload

interface JoinButtonCallback {
    fun onJoinButtonClicked(joinRoomSocketEventPayload:JoinRoomSocketEventPayload)
}