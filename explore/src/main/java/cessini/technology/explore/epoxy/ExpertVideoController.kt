package cessini.technology.explore.epoxy

import android.util.Log
import androidx.fragment.app.FragmentActivity
import cessini.technology.explore.viewmodel.ExploreSearchViewModel
import cessini.technology.model.HealthFitness
import cessini.technology.newrepository.video.VideoRepository
import com.airbnb.epoxy.AsyncEpoxyController

class ExpertVideoController(val activity: FragmentActivity?,
                            val viewModel: ExploreSearchViewModel,
) : AsyncEpoxyController() {
    var items: List<HealthFitness> = emptyList()
        set(value) {
            field = value
            requestModelBuild()
        }
    private val CORNER_RADIUS_MAX: Float = 20f
    override fun buildModels() {
        var i=0;
        var size = items.size
        items.forEachIndexed { index, it ->
            expert{
                Log.d("ExpertModel",it.toString())
                id(index)
                img(it.thumbnail)
                title1(it.title)
                user1(it.profile?.name)
                view1(it.category?.get(0))
                time1(it.duration)
                activity(activity)
                viewModel(viewModel)
            }
        }
    }
}