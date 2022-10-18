package cessini.technology.newapi.extensions

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Response

private val ResponseBody.message: String
    get() = runCatching {
        Gson().fromJson(string(), ErrorBody::class.java).message
    }.getOrDefault(defaultValue = "Error!")

data class ErrorBody(
    val message: String,
)

fun <T> Response<T>.getOrThrow(): T {
    if (!isSuccessful) throw Exception(errorBody()!!.message)

    return body()!!
}
