package cessini.technology.home.EpoxyModelClasses

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cessini.technology.home.R
import cessini.technology.home.model.JoinRoomSocketEventPayload
/*import cessini.technology.newrepository.preferences.main*/
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.hls.HlsDataSourceFactory
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.HandlerWrapper
import com.google.android.exoplayer2.util.Util

@EpoxyModelClass
abstract class HomeEpoxyModel(): EpoxyModelWithHolder<HomeEpoxyModel.Holder>() {

    private var playWhenReady=false

    @EpoxyAttribute
    lateinit var link:String

    @EpoxyAttribute
    lateinit var title:String

    @EpoxyAttribute
    lateinit var username:String

    @EpoxyAttribute
    lateinit var profileImageUrl:String

    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute
    lateinit var joinRoomSocketEventPayload: JoinRoomSocketEventPayload

    @EpoxyAttribute
    lateinit var onJoinButtonClicked:(JoinRoomSocketEventPayload)->Unit


    override fun getDefaultLayout(): Int {
        // return R.layout.item_home_fragment
        return R.layout.common_video_adapter
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title
        holder.username.text = username

        /*TODO: Ask Backend to give admin profile pic in API response
           if(profileImageUrl.isNotEmpty()) {
            Glide.with(context)
                .load(profileImageUrl)
                .into(holder.adminProfilePic)
        }*/

        holder.joinHub.setOnClickListener {
            onJoinButtonClicked(joinRoomSocketEventPayload)
            holder.joinHub.visibility = View.GONE
            holder.joinHubRequestSent.visibility = View.VISIBLE
        }
        initPlayer(holder.screen)
    }

    class Holder:EpoxyHolder(){
        lateinit var screen:PlayerView
        lateinit var title:TextView
        lateinit var username:TextView
        lateinit var joinHub:LinearLayout
        lateinit var joinHubRequestSent:LinearLayout
        lateinit var adminProfilePic:ImageView
        override fun bindView(itemView: View) {
           screen= itemView.findViewById(R.id.playerView)
           title=itemView.findViewById(R.id.profile_video_description)
           username=itemView.findViewById(R.id.profile_video_user_name)
           joinHub=itemView.findViewById(R.id.join_hub_btn)
           joinHubRequestSent = itemView.findViewById(R.id.join_hub_request_sent)
           adminProfilePic = itemView.findViewById(R.id.profile_video_user_image)
        }

    }
    private fun initPlayer(screen:PlayerView){
       val adaptiveTrackSelection=AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())

        val player=ExoPlayerFactory.newSimpleInstance(context,DefaultTrackSelector(context,adaptiveTrackSelection),DefaultLoadControl())
        screen.player = player
        val defaultBandwidthMeter=DefaultBandwidthMeter()
        val dataSourceFactory=DefaultDataSourceFactory(context,Util.getUserAgent(context,"Exo2"),defaultBandwidthMeter)
        val uri= Uri.parse(link)
        val mediaSource=HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        player.prepare(mediaSource)
        player.playWhenReady=playWhenReady

        player.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                super.onPlayerStateChanged(playWhenReady, playbackState)
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(context,"Exoplayer is not working",Toast.LENGTH_SHORT)
            }
        })
    }
}