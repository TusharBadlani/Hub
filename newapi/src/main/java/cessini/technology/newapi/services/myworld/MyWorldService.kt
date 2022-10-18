package cessini.technology.newapi.services.myworld

import cessini.technology.newapi.ApiParameters
import cessini.technology.newapi.model.ApiMessage
import cessini.technology.newapi.services.myworld.model.body.*
import cessini.technology.newapi.services.myworld.model.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MyWorldService {
    @Headers(ApiParameters.NO_AUTH_HEADER)
    @POST(MyWorldConstants.AUTH_ENDPOINT)
    suspend fun authenticate(
        @Body authBody: AuthBody,
    ): Response<ApiAuthenticate>

    @GET(value = MyWorldConstants.FOLLOWING_ENDPOINT)
    suspend fun getFollowing(
        @Query(value = "id") id: String,
    ): ApiFollowerFollowing

    @GET(value = MyWorldConstants.FOLLOWER_ENDPOINT)
    suspend fun getFollowers(
        @Query(value = "id") id: String
    ): Response<ApiFollowerFollowing>

    @POST(MyWorldConstants.FOLLOWING_ENDPOINT)
    suspend fun followUser(
        @Body apiUserId: UserIdBody,
    ): Response<ApiMessage>

    @HTTP(method = "DELETE", path = MyWorldConstants.FOLLOWING_ENDPOINT, hasBody = true)
    suspend fun unFollowUser(
        @Body apiUserId: UserIdBody,
    ): Response<ApiMessage>

    @GET(MyWorldConstants.PUBLIC_PROFILE_ENDPOINT)
    suspend fun getProfileWithFollowStatus(
        @Query(MyWorldParameters.ID) id: String,
    ): Response<ApiGetProfile>

    @Headers(ApiParameters.NO_AUTH_HEADER)
    @GET(MyWorldConstants.PUBLIC_PROFILE_ENDPOINT)
    suspend fun getProfile(
        @Query(MyWorldParameters.ID) id: String,
    ): Response<ApiGetProfile>

    @DELETE(MyWorldConstants.PROFILE_ENDPOINT)
    suspend fun deleteProfile(): Response<ApiMessage>

    @Multipart
    @PUT(MyWorldConstants.PROFILE_ENDPOINT)
    suspend fun updateProfile(
        @Part(MyWorldParameters.NAME) name: RequestBody,
        @Part(MyWorldParameters.BIO) bio: RequestBody,
        @Part(MyWorldParameters.LOCATION) location: RequestBody,
        @Part(MyWorldParameters.CHANNEL_NAME) channelName: RequestBody?,
        @Part(MyWorldParameters.EMAIL) email: RequestBody?,
        @Part(MyWorldParameters.AREA_OF_EXPERT) expertise: RequestBody?,
        @Part profile_picture: MultipartBody.Part?,
    ): Response<ApiMessage>

    @POST(MyWorldConstants.PROFILE_ENDPOINT)
    suspend fun channelNameAvailable(
        @Body channelNameBody: ChannelNameBody,
    ): Response<ApiMessage>

    @GET(MyWorldConstants.PROFILE_ENDPOINT)
    suspend fun getProfile(): Response<ApiGetProfile>

    @Headers(ApiParameters.NO_AUTH_HEADER)
    @GET(MyWorldConstants.PROFILE_MEDIA_ENDPOINT)
    suspend fun getProfileMedia(
        @Query(MyWorldParameters.ID) id: String,
    ): ApiProfileMedia

    @POST(MyWorldConstants.PROFILE_UPI_DATA)
    suspend fun saveUpiData(
        @Body upiData: UPIData,
    ): Response<ApiMessage>

    @PUT(MyWorldConstants.PROFILE_UPI_DATA)
    suspend fun updateUpiData(
        @Body upiData: UpdateUpiData,
    ): Response<ApiMessage>

    @GET(MyWorldConstants.PROFILE_UPI_DATA)
    suspend fun getCreatorUPIData(
        @Query("id") userId: String,
    ): ApiUpi

    @POST(MyWorldConstants.MESSAGE_USER)
    suspend fun sendMessageToUser(
        @Body roomRequestBody: CreateRoomRequestBody
    ) : Response<ApiMessage>

    @GET(MyWorldConstants.MESSAGE_USER)
    suspend fun getRoomMessages() : ApiCreateRoomMessage

    @POST(MyWorldConstants.ONBOARD_SUBMISSION)
    suspend fun submitOnBoardingSelection(
        @Body submission: OnBoardingSubmission
    ): OnBoardingSubmissionResponse
}
