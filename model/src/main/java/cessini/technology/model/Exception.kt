package cessini.technology.model

import java.io.IOException

//class NetworkUnavailableException(
//    message: String = "Network not available",
//) : IOException(message)

class NetworkUnavailableException : IOException() {
    override val message: String
        get() = "No Internet Connection"

}
