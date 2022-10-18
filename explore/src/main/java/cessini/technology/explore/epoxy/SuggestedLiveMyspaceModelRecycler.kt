package cessini.technology.explore.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.explore.R
import cessini.technology.explore.fragment.ExploreFragmentDirections
import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyController

@EpoxyModelClass
abstract class SuggestedLiveMyspaceModelRecycler: EpoxyModelWithHolder<SuggestedLiveMyspaceModelRecycler.Holder>() {
    @EpoxyAttribute
    lateinit var maincontroller: MultiGridController
    @EpoxyAttribute
    lateinit var categoryTitle :String
    @EpoxyAttribute
    lateinit var onClickListener: View.OnClickListener
    override fun bind(holder: Holder) {
        holder.recyclerView.setController(maincontroller)
        holder.textView.text= categoryTitle
        holder.recyclerView.isClickable=true
        holder.recyclerView.setOnClickListener(onClickListener)
        holder.textView.setOnClickListener(onClickListener)
        holder.imageView.setOnClickListener(onClickListener)
        holder.recyclerView.setOnClickListener {
            maincontroller.fragment.let {
                it.findNavController().navigate(
                    ExploreFragmentDirections.actionExploreFragmentToLiveFragment(
                        "Suggestion Rooms",
                        maincontroller.category
                    )
                )

            }
        }

    }

    override fun getDefaultLayout(): Int {
        return R.layout.suggestionlivemyspaceepoxy
    }
    class Holder : EpoxyHolder() {
        lateinit var recyclerView: EpoxyRecyclerView
        lateinit var textView: TextView
        lateinit var imageView: ImageView
        override fun bindView(itemView: View) {
            recyclerView = itemView.findViewById(R.id.horizontalepoxy)
            textView= itemView.findViewById(R.id.live_hub_category_title)
            imageView= itemView.findViewById(R.id.imageView9)
        }
    }
}