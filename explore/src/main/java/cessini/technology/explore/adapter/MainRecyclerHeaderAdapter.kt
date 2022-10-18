package cessini.technology.explore.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.explore.R
import cessini.technology.explore.databinding.SliderItemBinding
import cessini.technology.explore.viewmodel.SearchViewModel
import cessini.technology.model.PublicEvent
import com.airbnb.epoxy.databinding.BR


class MainRecyclerHeaderAdapter(
    var viewModel: SearchViewModel,
    private val categoryItem: List<PublicEvent>,
    private val pos: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<SliderItemBinding>(
            layoutInflater,
            R.layout.slider_item,
            parent,
            false
        )
        return CategoryItemViewHolder3(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as CategoryItemViewHolder3).bind(viewModel, pos, position)
        holder.binding.imageTitle = categoryItem[position].title
        holder.binding.imageDescription = categoryItem[position].description
        holder.binding.image = categoryItem[position].image

    }

    override fun getItemCount(): Int {
        return categoryItem.size
    }

    class CategoryItemViewHolder3(var binding: SliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: SearchViewModel, parentPosition: Int, position: Int) {

            binding.setVariable(BR.childItem3ViewModel, viewModel)
            binding.setVariable(BR.parentPosition, parentPosition)
            binding.setVariable(BR.position, position)

        }
    }

}
