package cessini.technology.newrepository.video

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import cessini.technology.model.Comment
import cessini.technology.model.Video
import cessini.technology.model.ViewAndDuration
import cessini.technology.newapi.extensions.getOrThrow
import cessini.technology.newapi.services.video.VideoService
import cessini.technology.newapi.services.video.model.body.VideoCommentBody
import cessini.technology.newapi.services.video.model.body.VideoIdBody
import cessini.technology.newrepository.extensions.createMultipartBody
import cessini.technology.newrepository.mappers.toModel
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class VideoRepository @Inject constructor(
    private val userIdentifierPreferences: UserIdentifierPreferences,
    private val videoService: VideoService,
) {
    suspend fun getViewAndDuration(id: String): ViewAndDuration {
        return videoService.viewsAndDuration(id).getOrThrow().data.toModel()
    }

    suspend fun detail(id: String): Video {
        return videoService.detail(id).data.toModel()
    }

    suspend fun like(id: String) {
        videoService.like(VideoIdBody(id))
    }

    suspend fun comment(videoId: String, text: String) {
        videoService.comment(VideoCommentBody(videoId, text))
    }

    suspend fun getComments(videoId: String): List<Comment> {
        return videoService.getComment(videoId).getOrThrow().data.comments.map { it.toModel() }
    }

    suspend fun liked(videoId: String): Boolean {
        return userIdentifierPreferences.loggedIn && videoService.liked(videoId).getOrThrow().data
    }

    suspend fun delete(videoId: String) {
        videoService.delete(videoId)
    }

    suspend fun upload(
        title: String,
        description: String,
        category: String,
        video: String,
        thumbnail: String
    ) {
        val duration = MediaMetadataRetriever().apply { setDataSource(video) }
            .extractMetadata(METADATA_KEY_DURATION)!!
            .toLong().div(other = 1000)

        videoService.upload(
            title = title.toRequestBody(MultipartBody.FORM),
            duration = duration.toString().toRequestBody(MultipartBody.FORM),
            description = description.toRequestBody(MultipartBody.FORM),
            category = category.toRequestBody(MultipartBody.FORM),
            thumbnail = File(thumbnail).createMultipartBody(
                formName = "thumbnail",
                contentType = "image/*"
            ),
            upload_file = File(video).createMultipartBody(
                formName = "upload_file",
                contentType = "video/mp4",
            )!!,
        )
    }
}
