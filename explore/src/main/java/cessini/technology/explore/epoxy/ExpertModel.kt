package cessini.technology.explore.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import cessini.technology.commonui.presentation.globalviewmodels.GalleryViewModel
import cessini.technology.cvo.exploremodels.CategoryModel
import cessini.technology.cvo.exploremodels.ProfileModel
import cessini.technology.cvo.exploremodels.SearchVideoModel
import cessini.technology.explore.R
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide

@EpoxyModelClass
abstract class ExpertModel : EpoxyModelWithHolder<ExpertModel.Holder>() {

    @EpoxyAttribute
    lateinit var img: String

    @EpoxyAttribute
    lateinit var title1: String

    @EpoxyAttribute
    lateinit var user1: String

    @EpoxyAttribute
    lateinit var view1: String

    @EpoxyAttribute
    lateinit var time1: String

    @EpoxyAttribute
    lateinit var activity: FragmentActivity

    @EpoxyAttribute
    lateinit var viewModel: ExploreSearchViewModel


    override fun getDefaultLayout(): Int {
        return R.layout.expert_view_item
    }

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder)
        {
            Glide.with(this.image.context).load(img).into(this.image)
            this.title.text = title1
            this.user.text = user1
            this.view.text = "${view1} views"
            this.duration.text = time1
            image.setOnClickListener {
                val galleryViewModel = activity.run {
                    ViewModelProvider(this)[GalleryViewModel::class.java]
                }
                //galleryViewModel.activity = viewModel.activity as FragmentActivity
                galleryViewModel.setVideoPos(0)
                galleryViewModel.setVideoFlow(0)
                //activity.lifecycleScope.launch {
                //runCatching {
                //val video = videoRepository.detail("")
                val dummy = SearchVideoModel(
                    id = "video.id",
                    title = "Tabish",
                    description = "video.description",
                    category = CategoryModel(1, "VideoCategory"),
                    duration = 0,
                    thumbnail = "video.thumbnail",
                    upload_file = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    //upload_file = "https://new-myworld-bucket.s3.amazonaws.com/Videos/VideoID678088500000000062_1639131472/VideoID678088500000000062_1639131472.mpd",
                    timestamp = "video.timestamp.toString()",
                    profile = ProfileModel(
                        id = "video.profile.id",
                        name = "video.profile.name",
                        channel_name = "video.profile.channel",
                        profile_picture = " video.profile.picture",
                    )
                )
                //}.onSuccess {
                android.util.Log.e("ViderController", dummy.toString())
                galleryViewModel.setVideoDisplay(dummy)
                (activity as ToFlowNavigable).navigateToFlow(
                    NavigationFlow.GlobalVideoDisplayFlow
                )
                // }
                //}
            }
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var image: ImageView
        lateinit var title: TextView
        lateinit var user: TextView
        lateinit var view: TextView
        lateinit var duration: TextView
        override fun bindView(itemView: View) {
            image = itemView.findViewById(R.id.img)
            title = itemView.findViewById(R.id.title)
            user = itemView.findViewById(R.id.hostname)
            view = itemView.findViewById(R.id.category)
            duration = itemView.findViewById(R.id.videoTime)
        }
    }

}