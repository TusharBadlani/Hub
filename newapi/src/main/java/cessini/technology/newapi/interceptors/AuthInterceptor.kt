package cessini.technology.newapi.interceptors

import cessini.technology.newapi.ApiParameters.AUTH_HEADER
import cessini.technology.newapi.ApiParameters.NO_AUTH
import cessini.technology.newapi.ApiParameters.TOKEN_TYPE
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newapi.services.myworld.MyWorldConstants.BASE_ENDPOINT
import cessini.technology.newapi.services.myworld.MyWorldConstants.REFRESH_TOKEN_ENDPOINT
import cessini.technology.newapi.services.myworld.model.body.RefreshBody
import cessini.technology.newapi.services.myworld.model.response.ApiAccessToken
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        // Skip requests with "No-Auth" header
        if (chain.request().headers[NO_AUTH] != null) {
            return chain.proceed(chain.request())
        }

        if (!authPreferences.tokenExpire) {
            return chain.proceedDeleteTokenIfUnauthorized(
                chain.createAuthRequest(authPreferences.accessToken)
            )
        }

        updateAccessToken(chain)

        return chain.proceedDeleteTokenIfUnauthorized(
            chain.createAuthRequest(authPreferences.accessToken)
        )
    }

    private fun updateAccessToken(chain: Interceptor.Chain) {
        authPreferences.accessToken =
            Gson().fromJson(
                chain.refreshAccessToken().run {
                    body!!.string().also { close() }
                },
                ApiAccessToken::class.java
            )?.data?.access ?: ""
    }

    private fun Interceptor.Chain.proceedDeleteTokenIfUnauthorized(request: Request): Response {
        val response = proceed(request)

        if (response.code == 401) {
            authPreferences.deleteAccessToken()
        }

        return response
    }

    private fun Interceptor.Chain.createAuthRequest(accessToken: String): Request {
        return request()
            .newBuilder()
            .addHeader(AUTH_HEADER, TOKEN_TYPE + accessToken)
            .build()
    }

    private fun Interceptor.Chain.refreshAccessToken(): Response {
        val body = Gson().toJson(RefreshBody(authPreferences.refreshToken))
            .toRequestBody("application/json".toMediaType())

        val tokenRefresh = request()
            .newBuilder()
            .url("$BASE_ENDPOINT$REFRESH_TOKEN_ENDPOINT")
            .post(body)
            .build()

        return proceedDeleteTokenIfUnauthorized(tokenRefresh)
    }
}
