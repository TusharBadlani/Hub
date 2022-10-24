package cessini.technology.home.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.epoxy.homegrid.story.ChildRecyclerController
import cessini.technology.home.R
import cessini.technology.commonui.epoxy.homegrid.grid.GridController2
import cessini.technology.commonui.presentation.auth.SignInFragment
import cessini.technology.model.RoomViews
import cessini.technology.model.Viewer
import cessini.technology.model.ViewerX
import cessini.technology.myspace.access.MySpaceFragmentViewModel
import cessini.technology.navigation.Navigator
import cessini.technology.newapi.model.CreatorListeners
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.newrepository.websocket.video.RoomViewerUpdaterWebSocket
import com.airbnb.epoxy.EpoxyRecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class RoomPagerAdapter(
    var roomList: ArrayList<ArrayList<CreatorListeners>>,
    var titleList:ArrayList<String>,
    val liked: HashMap<String,Boolean>,
    var context: Context,
    val activity: Activity,
    val fragment:Fragment,
    val fragmentManager: FragmentManager,
    val screenWidth: Int,
    val screenHeight: Int,
    val resList:MutableList<ViewerX>,
    val type:Int
) : RecyclerView.Adapter<RoomPagerAdapter.ViewPagerHolder>() {
    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    @Inject
    lateinit var authPreferences: AuthPreferences

    val viewModel = fragment.run {
        ViewModelProvider(this)[MySpaceFragmentViewModel::class.java]
    }

    //private var repo: TimelineRepository = TimelineRepository()
//    private var roomTimelineLive  = MutableLiveData<RoomTimeline>()
    private var roomViews= MutableLiveData<RoomViews>()

    @Inject
    lateinit var roomViewerUpdaterWebSocket: RoomViewerUpdaterWebSocket

    @Inject
    lateinit var navigator: Navigator
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_viewpager_room, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
         authPreferences = AuthPreferences(context)
        userIdentifierPreferences= UserIdentifierPreferences(context, authPreferences)
        roomViewerUpdaterWebSocket= RoomViewerUpdaterWebSocket(authPreferences,userIdentifierPreferences)
        navigator= Navigator()
        navigator.activity= activity
        navigator.navController= NavController(context)
        holder.joinMyspace.setOnClickListener()
        {
            login()
            return@setOnClickListener
        }
        setLiked(holder.button1,titleList[position])


        holder.share.setOnClickListener {
            login()
            openSharingVia(titleList[position])
        }

        holder.button1.setOnClickListener {
            login()
            if(userIdentifierPreferences.loggedIn) {
                setLikedClicked(holder.button1, titleList[position])
            }
        }

        val layoutManager = GridLayoutManager(context, 2)
        val controller: GridController2 = GridController2(screenHeight,screenWidth,context)
        holder.epoxyRecycler.layoutManager = layoutManager
        holder.epoxyRecycler.setController(controller)

        var storyItems = listOf<Viewer>(
            Viewer("3","ID335167180000000097","","https://new-myworld-bucket.s3.amazonaws.com/user_files/ID335167180000000097/profile/picture1644639460947.jpg\\",2,"",5f)
        )

        var newStoryItems = ArrayList<Viewer>()
        resList.forEach {
            newStoryItems.add(Viewer(it._id,it.profile.channel_name,"",it.profile.profile_picture,1,"",1f))
        }

        Log.d("roomviews","reslist is $resList")

        //controller.listViewer=listViewers
        Log.d("Heslo4","RoomPagerAdapter")
          Log.d("Heslo4",roomList[position].toString())
        controller.creatorAndListeners= roomList[position]
        val childcontroller : ChildRecyclerController = ChildRecyclerController(context)
//        holder.storyRecycler.setController(childcontroller)
//        childcontroller.viewerItems= newStoryItems
        addImages(holder,newStoryItems)
    }

    private fun setLikedClicked(button1: ImageView, title: String) {
        if(liked.get(title)!=null && liked.get(title)==false){
            viewModel.likeRoom(title,userIdentifierPreferences.id)
            button1.setImageResource(R.drawable.ic_likeactive)
            liked.put(title,true)
        }
        else if(liked.get(title)!=null && liked.get(title)==true){
            viewModel.likeRoom(title,userIdentifierPreferences.id)
            button1.setImageResource(R.drawable.ic_like_inactive)
            liked.put(title,false)
        }
    }

    private fun setLiked(button1: ImageView, title: String) {
        if(liked.get(title)!=null && liked.get(title)==true){
            button1.setImageResource(R.drawable.ic_likeactive)
        }
        else {
            button1.setImageResource(R.drawable.ic_like_inactive)
        }
    }

    private fun addImages(holder: ViewPagerHolder,newStoryItems: ArrayList<Viewer>) {
        if(newStoryItems.size==1)
        {
            setCircularImageView(holder.image1, newStoryItems[0].thumbnail)
            holder.image2.isVisible=false
            holder.image3.isVisible=false
            holder.roomviewers.isVisible=false
        }
        else if(newStoryItems.size==2)
        {
            setCircularImageView(holder.image1, newStoryItems[0].thumbnail)
            setCircularImageView(holder.image2, newStoryItems[1].thumbnail)
            holder.image3.isVisible=false
            holder.roomviewers.isVisible=false
        }
        else if(newStoryItems.size>2)
        {
            setCircularImageView(holder.image1, newStoryItems[0].thumbnail)
            setCircularImageView(holder.image2, newStoryItems[1].thumbnail)
            setCircularImageView(holder.image2, newStoryItems[1].thumbnail)
            if(newStoryItems.size>3)
               holder.roomviewers.text= "+${newStoryItems.size-3} "
            else
                holder.roomviewers.isVisible=false
        }
    }
    fun setCircularImageView(view: CircleImageView?, url: String?) {
        if (view != null) {
            Glide
                .with(view.context)
                .load(url)
                .error(R.drawable.ic_user_defolt_avator)
                .centerCrop()
                .into(view)
        }
    }

    private fun login() {
        if (!userIdentifierPreferences.loggedIn) {
            goToAuth()
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

    inner class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var storyRecycler:EpoxyRecyclerView
        var epoxyRecycler: EpoxyRecyclerView
        var joinMyspace: TextView
        var share: ImageView
        var button1:ImageView
        var image1:CircleImageView
        var image2:CircleImageView
        var image3:CircleImageView
        var roomviewers:TextView

        val listViewers = ArrayList<List<Viewer>>()

        init {
          //  storyRecycler= itemView.findViewById(R.id.recyclerViewChild)
            epoxyRecycler = itemView.findViewById(R.id.recycler_view)
            button1= itemView.findViewById(R.id.imageView21)
            share = itemView.findViewById(R.id.imageView22)
            joinMyspace= itemView.findViewById(R.id.join_button)
            image1= itemView.findViewById(R.id.myspace_iv3_home)
            image2= itemView.findViewById(R.id.myspace_iv4_home)
            image3= itemView.findViewById(R.id.myspace_iv5_home)
            roomviewers= itemView.findViewById(R.id.room_title_home)
        }

    }
    fun goToAuth() {
        val fragment = SignInFragment()
        fragment.show(fragmentManager, fragment.tag)



    }

    private fun openSharingVia(rname: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val shareBody="https://www.myworld.com/liveRoom?code=$rname"
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            R.string.share_room
        )

        intent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(context,Intent.createChooser(intent, "Send"),null)
//        val sendIntent: Intent = Intent().apply {
//            action = Intent.ACTION_SEND
//            val message =
//                "Let's talk with best creators who help you on Myworld! It's a fast, simple and secure app. Get it at "
//            val link = "https://play.google.com/store/apps/details?id=cessini.technology.myworld"
//            putExtra(Intent.EXTRA_TEXT, message + link)
//            type = "text/plain"
//        }
//
//        val shareIntent = Intent.createChooser(sendIntent, "Invite via")
//        ContextCompat.startActivity(context, shareIntent, null)
    }

}