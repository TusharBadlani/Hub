package cessini.technology.cvo.authmodels

data class GoogleAuthResponseBody(
    var profile: Profile,
    var token: Tokens
)