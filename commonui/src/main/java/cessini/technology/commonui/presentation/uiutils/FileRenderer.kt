package cessini.technology.commonui.presentation.uiutils

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import org.webrtc.*
import java.nio.ByteBuffer


class FileRenderer(outputFile: String?, sharedContext: EglBase.Context) :
    VideoSink {
    private val renderThread: HandlerThread = HandlerThread(TAG + "RenderThread")
    private val renderThreadHandler: Handler
    private var outputFileWidth = -1
    private var outputFileHeight = -1
    private lateinit var encoderOutputBuffers: Array<ByteBuffer>
    private var eglBase: EglBase? = null
    private val sharedContext: EglBase.Context
    private var frameDrawer: VideoFrameDrawer? = null
    private val mediaMuxer: MediaMuxer
    private var encoder: MediaCodec? = null
    private val bufferInfo: MediaCodec.BufferInfo
    private var trackIndex = -1
    private var isRunning = true
    private var drawer: GlRectDrawer? = null
    private var surface: Surface? = null
    private fun initVideoEncoder() {
        val format = MediaFormat.createVideoFormat(MIME_TYPE, 1280, 720)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, 6000000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL)
        try {
            encoder = MediaCodec.createEncoderByType(MIME_TYPE)
            encoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            renderThreadHandler.post {
                eglBase = EglBase.create(sharedContext, EglBase.CONFIG_RECORDABLE)
                surface = encoder!!.createInputSurface()
                eglBase!!.createSurface(surface)
                eglBase!!.makeCurrent()
                drawer = GlRectDrawer()
            }
        } catch (e: Exception) {
            Log.wtf(TAG, e)
        }
    }

    override fun onFrame(frame: VideoFrame) {
        frame.retain()
        if (outputFileWidth == -1) {
            outputFileWidth = frame.rotatedWidth
            outputFileHeight = frame.rotatedHeight
            initVideoEncoder()
        }
        renderThreadHandler.post { renderFrameOnRenderThread(frame) }
    }

    private fun renderFrameOnRenderThread(frame: VideoFrame) {
        if (frameDrawer == null) {
            frameDrawer = VideoFrameDrawer()
        }
        frameDrawer!!.drawFrame(frame, drawer, null, 0, 0, outputFileWidth, outputFileHeight)
        frame.release()
        drainEncoder()
        eglBase!!.swapBuffers()
    }

    /**
     * Release all resources. All already posted frames will be rendered first.
     */
    fun release() {
        isRunning = false
        renderThreadHandler.post {
            if (encoder != null) {
                encoder!!.stop()
                encoder!!.release()
            }
            eglBase!!.release()
            mediaMuxer.stop()
            mediaMuxer.release()
            renderThread.quit()
        }
    }

    private var encoderStarted = false

    @Volatile
    private var muxerStarted = false
    private var videoFrameStart: Long = 0
    private fun drainEncoder() {
        if (!encoderStarted) {
            encoder!!.start()
            encoderOutputBuffers = encoder!!.outputBuffers
            encoderStarted = true
            return
        }
        while (true) {
            val encoderStatus = encoder!!.dequeueOutputBuffer(bufferInfo, 10000)
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = encoder!!.outputBuffers
                Log.e(TAG, "encoder output buffers changed")
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // not expected for an encoder
                val newFormat = encoder!!.outputFormat
                Log.e(
                    TAG,
                    "encoder output format changed: $newFormat"
                )
                trackIndex = mediaMuxer.addTrack(newFormat)
                if (!muxerStarted) {
                    mediaMuxer.start()
                    muxerStarted = true
                }
                if (!muxerStarted) break
            } else if (encoderStatus < 0) {
                Log.e(
                    TAG,
                    "unexpected result fr om encoder.dequeueOutputBuffer: $encoderStatus"
                )
            } else { // encoderStatus >= 0
                try {
                    val encodedData = encoderOutputBuffers[encoderStatus]
                    if (encodedData == null) {
                        Log.e(
                            TAG,
                            "encoderOutputBuffer $encoderStatus was null"
                        )
                        break
                    }
                    // It's usually necessary to adjust the ByteBuffer values to match BufferInfo.
                    encodedData.position(bufferInfo.offset)
                    encodedData.limit(bufferInfo.offset + bufferInfo.size)
                    if (videoFrameStart == 0L && bufferInfo.presentationTimeUs != 0L) {
                        videoFrameStart = bufferInfo.presentationTimeUs
                    }
                    bufferInfo.presentationTimeUs -= videoFrameStart
                    if (muxerStarted) mediaMuxer.writeSampleData(
                        trackIndex,
                        encodedData,
                        bufferInfo
                    )
                    isRunning =
                        isRunning && bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM == 0
                    encoder!!.releaseOutputBuffer(encoderStatus, false)
                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                        break
                    }
                } catch (e: Exception) {
                    Log.wtf(TAG, e)
                    break
                }
            }
        }
    }

    private val presTime = 0L

    companion object {
        private const val TAG = "FileRenderer"
        private const val MIME_TYPE = "video/avc" // H.264 Advanced Video Coding
        private const val FRAME_RATE = 30 // 30fps
        private const val IFRAME_INTERVAL = 5 // 5 seconds between I-frames
    }

    init {
        renderThread.start()
        renderThreadHandler = Handler(renderThread.looper)
        bufferInfo = MediaCodec.BufferInfo()
        this.sharedContext = sharedContext
        mediaMuxer = MediaMuxer(
            outputFile!!,
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )
    }
}