package cessini.technology.newrepository.myworld

import cessini.technology.model.*
import cessini.technology.newapi.services.myworld.model.body.UPIData
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.myworld.MyWorldService
import cessini.technology.newapi.services.myworld.model.body.ChannelNameBody
import cessini.technology.newapi.services.myworld.model.body.CreateRoomRequestBody
import cessini.technology.newapi.services.myworld.model.body.UpdateUpiData
import cessini.technology.newapi.services.myworld.model.response.ApiCreateRoomMessage
import cessini.technology.newrepository.datastores.ProfileStore
import cessini.technology.newrepository.extensions.createMultipartBody
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.mappers.toModels
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileStore: ProfileStore,
    private val myWorldService: MyWorldService,
) {
    val profile = profileStore.getProfile()

    suspend fun getProfile(id: String): Profile =
        myWorldService.getProfile(id).getOrThrow().data.toModel()

    suspend fun getProfileRooms(id: String): List<Room>? {
        return myWorldService.getProfileMedia(id).toModel().rooms
    }

    suspend fun getProfile(): Profile {
        val profile = myWorldService.getProfile().getOrThrow().data.toModel()
        profileStore.storeProfile(profile)
        return profile
    }

    suspend fun getProfileWithFollowingStatus(id: String): Profile =
        myWorldService.getProfileWithFollowStatus(id).getOrThrow().data.toModel()

    suspend fun deleteProfile() {
        myWorldService.deleteProfile()
        profileStore.deleteProfile()
    }

    suspend fun channelNameAvailable(name: String): Boolean {
        return myWorldService.channelNameAvailable(ChannelNameBody(name))
            .code() != CHANNEL_NAME_TAKEN_CODE
    }

    suspend fun getProfileMedia(id: String): ProfileMedia {
        return myWorldService.getProfileMedia(id).toModel()
    }

    suspend fun updateProfile(
        name: String,
        bio: String,
        location: String,
        channelName: String,
        profilePicture: String,
        email: String,
        expertise: String
    ) {
        myWorldService.updateProfile(
            name = name.toRequestBody("text/plain".toMediaType()),
            email = email.ifEmpty { null }?.toRequestBody("text/plain".toMediaType()),
            expertise = expertise.ifEmpty { null }?.toRequestBody("text/plain".toMediaType()),
            bio = bio.toRequestBody("text/plain".toMediaType()),
            location = location.toRequestBody("text/plain".toMediaType()),
            channelName = channelName.ifEmpty { null }?.toRequestBody("text/plain".toMediaType()),
            profile_picture = File(profilePicture).createMultipartBody(
                formName = "profile_picture",
                contentType = "image/*"
            ),
        )
    }

    suspend fun saveUPIData(userId: String, upiId: String, upiName: String): Boolean {
        return myWorldService.saveUpiData(UPIData(userId, upiId, upiName)).isSuccessful
    }

    suspend fun updateUPIData(upiId: String, upiName: String): Boolean {
        return myWorldService.updateUpiData(UpdateUpiData(upiId, upiName)).isSuccessful
    }

    suspend fun getCreatorUPIData(userId: String): UserUpiData {
        return myWorldService.getCreatorUPIData(userId).data.toModels()
    }

    suspend fun sendMessageToUser(message: String, userId: String): Boolean {
        return myWorldService.sendMessageToUser(CreateRoomRequestBody(userId, message)).isSuccessful
    }

    suspend fun getMessageForRoom(): List<CreateRoomRequest> {
        return myWorldService.getRoomMessages().data.map { it.toModels() }
    }

    companion object {
        private const val CHANNEL_NAME_TAKEN_CODE = 302
    }
}
