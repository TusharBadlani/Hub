package cessini.technology.newapi.services.story

import cessini.technology.newapi.ApiParameters
import cessini.technology.newapi.model.ApiMessage
import cessini.technology.newapi.services.story.model.body.StoryIdBody
import cessini.technology.newapi.services.story.model.response.ApiGetStories
import cessini.technology.newapi.services.story.model.response.ApiViewAndDurationData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StoryService {
    @POST(StoryConstants.LIKE_ENDPOINT)
    suspend fun like(
        @Body storyIdBody: StoryIdBody,
    ): ApiMessage

    @Multipart
    @POST(StoryConstants.UPLOAD_ENDPOINT)
    suspend fun upload(
        @Part(value = StoryParameters.TITLE) title: RequestBody?,
        @Part(value = StoryParameters.DURATION) duration: RequestBody?,
        @Part(value = StoryParameters.CAPTION) caption: RequestBody?,
        @Part(value = StoryParameters.CATEGORY) category: RequestBody?,
        @Part thumbnail: MultipartBody.Part?,
        @Part upload_file: MultipartBody.Part,
    ): ApiMessage

    @Headers(ApiParameters.NO_AUTH_HEADER)
    @GET(value = "${StoryConstants.VIEW_ENDPOINT}{id}")
    suspend fun getViewAndDuration(@Path(value = "id") id: String): Response<ApiViewAndDurationData>
}

interface StoryGetService {
    @Headers(ApiParameters.NO_AUTH_HEADER)
    @GET(StoryConstants.GET_STORY_ENDPOINT)
    suspend fun getStories(): ApiGetStories
}
