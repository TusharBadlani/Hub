package cessini.technology.newrepository.story

import android.media.MediaMetadataRetriever
import android.util.Log
import cessini.technology.model.ProfileStories
import cessini.technology.model.ViewAndDuration
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.story.StoryGetService
import cessini.technology.newapi.services.story.StoryService
import cessini.technology.newapi.services.story.model.body.StoryIdBody
import cessini.technology.newrepository.extensions.createMultipartBody
import cessini.technology.newrepository.mappers.toModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val storyService: StoryService,
    private val storyGetService: StoryGetService,
) {
    suspend fun like(id: String) {
        storyService.like(StoryIdBody(id))
    }

    suspend fun getViewAndDuration(id: String): ViewAndDuration {
        return storyService.getViewAndDuration(id).getOrThrow().data.toModel()
    }

    suspend fun getStories(): List<ProfileStories> {
        return storyGetService.getStories().data.map { it.toModel() }
    }

    suspend fun upload(
        caption: String = "",
        thumbnail: String,
        story: String,
    ) {
        val duration = MediaMetadataRetriever().apply { setDataSource(story) }
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
            .toLong().div(other = 1000)

        storyService.upload(
            title = "story".toRequestBody(MultipartBody.FORM),
            caption = caption.toRequestBody(MultipartBody.FORM),
            duration = duration.toString().toRequestBody(MultipartBody.FORM),
            category = "1".toRequestBody(MultipartBody.FORM),
            thumbnail = File(thumbnail).createMultipartBody(
                formName = "thumbnail",
                contentType = "image/*",
            ),
            upload_file = File(story).createMultipartBody(
                formName = "upload_file",
                contentType = "video/mp4",
            )!!,
        )
    }
}
