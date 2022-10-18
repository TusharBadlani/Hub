package cessini.technology.cvo.authmodels

data class User(
    var id: Int,
    var username: String = "",
    var email: String = ""
)