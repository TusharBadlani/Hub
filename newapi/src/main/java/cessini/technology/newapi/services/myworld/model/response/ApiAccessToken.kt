package cessini.technology.newapi.services.myworld.model.response

data class ApiAccessToken(
    val data: AccessData,
)

data class AccessData(
    val access: String,
)
