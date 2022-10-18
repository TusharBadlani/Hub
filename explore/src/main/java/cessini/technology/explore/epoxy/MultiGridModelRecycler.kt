package cessini.technology.explore.epoxy

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import cessini.technology.explore.R
//import cessini.technology.myspace.generated.callback.OnClickListener
import com.airbnb.epoxy.*
import com.airbnb.epoxy.EpoxyController


@EpoxyModelClass
abstract class MultiGridModelRecycler : EpoxyModelWithHolder<MultiGridModelRecycler.Holder>() {
    @EpoxyAttribute
    lateinit var controller: EpoxyController
  @EpoxyAttribute
  var size :Int =0
    @EpoxyAttribute
    lateinit var roomTitle :String
    @EpoxyAttribute
    lateinit var clickListener: View.OnClickListener
    @EpoxyAttribute
    lateinit var ontouch:View.OnTouchListener
    override fun bind(holder: Holder) {
        holder.recyclerView.setController(controller)
        var layoutManager: GridLayoutManager = GridLayoutManager(holder.recyclerView.context, 2)
        if (size <= 2)
            layoutManager = GridLayoutManager(holder.recyclerView.context, 1)
        else if (size % 2 != 0) {
            layoutManager = GridLayoutManager(holder.recyclerView.context, 2)

            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    when (position) {
                        0 -> return 2
                        else -> return 1
                    }
                }
            }
        }
        holder.recyclerView.layoutManager= layoutManager
        holder.textView.text= roomTitle
        holder.textView.setOnClickListener(clickListener)
        holder.recyclerView.isClickable=true
        holder.recyclerView.setOnClickListener(clickListener)

    }

    override fun getDefaultLayout(): Int {
        return R.layout.gridepoxy
    }
    class Holder : EpoxyHolder() {
        lateinit var recyclerView: EpoxyRecyclerView
        lateinit var textView: TextView
        override fun bindView(itemView: View) {
            recyclerView = itemView.findViewById(R.id.samplepoxyview)
            textView= itemView.findViewById(R.id.roomtitle)
        }
    }
}