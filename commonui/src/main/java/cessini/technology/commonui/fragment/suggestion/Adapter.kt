package cessini.technology.commonui.fragment.suggestion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.commonui.R
import cessini.technology.commonui.databinding.ActivityHomeBinding.bind
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Adapter: RecyclerView.Adapter<CategoryViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<ChipGroup>(){
        override fun areItemsTheSame(oldItem: ChipGroup, newItem: ChipGroup): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ChipGroup, newItem: ChipGroup): Boolean {
            return oldItem.equals(newItem)
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chip_item,parent,false))
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = differ.currentList[position]
        (category.parent as? ViewGroup)?.removeView(category)
        holder.chipGroup.addView(category)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}


class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val chipGroup = itemView.findViewById<ChipGroup>(R.id.chip)
}
