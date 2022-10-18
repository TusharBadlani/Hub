package cessini.technology.home.webSockets

interface SocketEventCallback {
    fun onJoinRequestAccepted(socketId: String)
    fun onJoinRequestDenied(msg: String)
}