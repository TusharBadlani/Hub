package cessini.technology.newrepository.mappers

import cessini.technology.model.UserUpiData
import cessini.technology.newapi.services.myworld.model.response.ApiUpiData

fun ApiUpiData.toModels() = UserUpiData(
    upiId = upiId,
    upiName = upiName
)