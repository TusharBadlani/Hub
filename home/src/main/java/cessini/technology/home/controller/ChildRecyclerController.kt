//package cessini.technology.home.controller
//
//import android.content.Context
//import cessini.technology.home.R
//import cessini.technology.model.Viewer
//import com.airbnb.epoxy.AsyncEpoxyController
//import com.example.experiment.epoxy.story
//
//class ChildRecyclerController(var context: Context) : AsyncEpoxyController() {
//
//    var viewerItems: List<Viewer> = emptyList()
//        set(value) {
//            field = value
//            requestModelBuild()
//        }
//
//    override fun buildModels() {
//        for (i in 0..viewerItems.size - 1) {
//            story {
//                id(i)
//                context(context)
//                drawable(context.getDrawable(R.drawable.person_1))
//            }
//        }
//    }
//}