package cessini.technology.newapi.services.myworld.model.response

data class ApiUpiData(
    val id: String,
    val upiId: String,
    val upiName: String
)

data class ApiUpi(
    val data: ApiUpiData
)
