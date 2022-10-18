package cessini.technology.explore.epoxy

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import cessini.technology.commonui.activity.HomeActivity
import cessini.technology.commonui.utils.ProfileConstants
import cessini.technology.cvo.exploremodels.SearchCreatorApiResponse
import cessini.technology.explore.R
import cessini.technology.explore.states.ExploreOnClickEvents
import cessini.technology.explore.viewmodel.SearchViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import cessini.technology.newrepository.myworld.ProfileRepository
import cessini.technology.profile.viewmodel.PublicProfileViewModel
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import java.util.HashMap
import javax.inject.Inject

@EpoxyModelClass
abstract class VoiceModel : EpoxyModelWithHolder<VoiceModel.Holder>() {


    @EpoxyAttribute
    lateinit var img: String

    @EpoxyAttribute
    lateinit var title1: String

    @EpoxyAttribute
    lateinit var vid: String

    @EpoxyAttribute
    lateinit var channelName: String

    @EpoxyAttribute
    lateinit var activity: FragmentActivity

    @EpoxyAttribute
    lateinit var followingStatusId: HashMap<String, Boolean>

    @EpoxyAttribute
    lateinit var onClickEvents: (event: ExploreOnClickEvents) -> Unit

    override fun getDefaultLayout(): Int {
        return R.layout.child_item_1
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
            val publicProfileViewModel = activity.run {
                ViewModelProvider(this)[PublicProfileViewModel::class.java]
            }
            var signedIn = (activity as HomeActivity).baseViewModel.authFlag.value
            followButton(btn)
            Glide.with(this.image.context).load(img).into(this.image)
            this.title.text = title1
            image.setOnClickListener {
                onClickEvents(ExploreOnClickEvents.ToGlobalProfileFlow)
            }
            btn.setOnClickListener {
                publicProfileViewModel.activity = activity
                Log.e("VoiceModel", "ButtonClickListenerCalled Signed=${signedIn}")
                publicProfileViewModel.onFollowClickExplore(vid)
                if (signedIn == true)
                    followButtonClick(btn)
            }
        }

    }

    class Holder : EpoxyHolder() {
        lateinit var image: ImageView
        lateinit var title: TextView
        lateinit var btn: Button
        override fun bindView(itemView: View) {
            image = itemView.findViewById(R.id.imageView)
            title = itemView.findViewById(R.id.textView)
            btn = itemView.findViewById(R.id.follow)
        }
    }


    private fun followButton(btn: Button) {
        if (followingStatusId.get(vid) != null && followingStatusId.get(vid) == true) {
            btn.text = "Unfollow"
            btn.setBackgroundResource(R.drawable.unfollow_btn)
            btn.setTextColor(ContextCompat.getColor(activity, R.color.cpTextDark))
            //btn.setTextColor(Color.rgb(118,118,118))
        } else {
            btn.text = "Follow"
            btn.setTextColor(ContextCompat.getColor(activity, R.color.btnfllow))
            //btn.setTextColor(Color.rgb(239,239,239))
            btn.setBackgroundResource(R.drawable.join_myspace_upcoming)
        }
    }

    private fun followButtonClick(btn: Button) {
        if (followingStatusId.get(vid) == false) {
            btn.text = "Unfollow"
            btn.setTextColor(ContextCompat.getColor(activity, R.color.cpTextDark))
            //btn.setTextColor(Color.rgb(118,118,118))
            btn.setBackgroundResource(R.drawable.unfollow_btn)
            followingStatusId.put(vid, true)
        } else {
            btn.text = "Follow"
            btn.setTextColor(ContextCompat.getColor(activity, R.color.btnfllow))
            //btn.setTextColor(Color.rgb(239,239,239))
            btn.setBackgroundResource(R.drawable.join_myspace_upcoming)
            followingStatusId.put(vid, false)
        }
    }
}
