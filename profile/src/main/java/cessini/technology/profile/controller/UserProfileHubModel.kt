package cessini.technology.profile.controller

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.commonui.presentation.common.dayTime
import cessini.technology.model.Room
import cessini.technology.model.RoomUsers
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.myspace.RoomRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.profile.R
import com.airbnb.epoxy.*
import kotlinx.coroutines.launch

@EpoxyModelClass
abstract class UserProfileHubModel: EpoxyModelWithHolder<UserProfileHubModel.Holder>() {

    @EpoxyAttribute
    lateinit var room: Room

    @EpoxyAttribute
    lateinit var context: Context

    @EpoxyAttribute
    lateinit var fragment: Fragment

    @EpoxyAttribute
    lateinit var activity: FragmentActivity

    @EpoxyAttribute
    lateinit var roomRepository: RoomRepository

    @EpoxyAttribute
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    override fun getDefaultLayout(): Int {
        return R.layout.user_hub_profile
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder){
            if(room.live){
                this.live.text = "Live"
                this.live.setBackgroundColor(ContextCompat.getColor(context,R.color.cpRed))
                this.live.setBackgroundResource(R.drawable.squer_roundview_live)
            }
            if(!room.live){
                this.live.text = "Upcoming"
                this.live.setTextColor(ContextCompat.getColor(context,R.color.cpHelpText))
                this.live.setBackgroundColor(ContextCompat.getColor(context,R.color.cpHelpText))
                this.live.setBackgroundResource(R.drawable.squer_roundview_gray)
            }

            var text = ""
            var categorytext = ""
            if (room.categories.isNotEmpty()) {
                val category = StringBuilder()
                room.categories.forEach {
                    category.append(categories[it.toInt()] + "@")
                }
                val fh = category.indexOf("@")
                val sh = category.indexOf("@", fh + 1)
                val th = category.indexOf("@", sh + 1)
                categorytext += "@" + category.substring(1, fh) + " "
                if (sh != -1) {
                    categorytext += "@" + category.substring(fh + 2, sh) + " "

                } else

                    if (th != -1) {
                        categorytext += "@" + category.substring(sh + 2, th) + " "
                    }
            }
            text = context!!.getString(R.string.some_text, room.title, categorytext)
            this.roomTitle.text = (HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY))

            this.hostTime.text = room.creator.name

            if(room.time==0L){
                this.roomTime.visibility = View.GONE
            }
            else{
                this.roomTime.visibility = View.VISIBLE
                this.roomTime.text = room.time.dayTime()
            }

            //Setting Grid Recycler View
            val gridList: MutableList<RoomUsers> = mutableListOf()
            room.creator.let {
                gridList.add(
                    RoomUsers(
                        it.id,
                        room.title,
                        it.name,
                        it.profilePicture,
                        it.channelName
                    )
                )

            }
            room.listeners.forEach {
                gridList.add(
                    RoomUsers(
                        it._id,
                        room.title,
                        it.name,
                        it.profile_picture,
                        it.channel_name
                    )
                )
            }
            var layoutManager: GridLayoutManager =
                GridLayoutManager(fragment.context, 2)
            val size = gridList.size
            if (size <= 2)
                layoutManager = GridLayoutManager(fragment.context, 1)
            else if (size % 2 != 0) {
                layoutManager = GridLayoutManager(fragment.context, 2)

                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        when (position) {
                            0 -> return 2
                            else -> return 1
                        }
                    }
                }
            }
            val controller = EpoxyControllerProfile(130, 90, "", fragment, false)
            controller.roomUsers = gridList
            this.gridEpoxyView.apply {
                setLayoutManager(layoutManager)
                setController(controller)
            }

            this.joinButton.setOnClickListener { view->
                if (!userIdentifierPreferences.loggedIn) {
                            (activity as ToFlowNavigable).navigateToFlow(NavigationFlow.AuthFlow)

                        } else {
                    activity.lifecycleScope.launch {
                        runCatching { roomRepository.joinRoom(room.name) }
                            .onSuccess {
                                Toast.makeText(
                                    activity,
                                    "Join request sent",
                                    Toast.LENGTH_SHORT
                                ).show()
                                view.setBackgroundResource(R.drawable.round_enable_viewbutton)
                            }
                    }
                }
            }
        }
    }

    class Holder: EpoxyHolder(){
        lateinit var gridEpoxyView: EpoxyRecyclerView
        lateinit var live: TextView
        lateinit var joinButton: Button
        lateinit var roomTitle: TextView
        lateinit var hostTime: TextView
        lateinit var roomTime: TextView
        lateinit var host: TextView
        override fun bindView(itemView: View) {
            gridEpoxyView = itemView.findViewById(R.id.samplepoxyviewp)
            live = itemView.findViewById(R.id.textView29p)
            joinButton = itemView.findViewById(R.id.joinRoomp)
            roomTitle = itemView.findViewById(R.id.upcoming_room_titlep)
            hostTime = itemView.findViewById(R.id.upcoming_room_host_timep)
            roomTime = itemView.findViewById(R.id.upcoming_room_timep)
            host = itemView.findViewById(R.id.textView30p)
        }

    }

    val categories = listOf(
        " Film and animation ",
        " Cars and vehicles ",
        " Music ",
        " Pets and animals ",
        " Sport ",
        " Travel and events ",
        " Gaming ",
        " People and blogs ",
        " Comedy ",
        " Entertainment ",
        " News and politics ",
        " How-to and style ",
        " Education ",
        " Science and technology ",
        " Non-profits and activism ",
    )
}