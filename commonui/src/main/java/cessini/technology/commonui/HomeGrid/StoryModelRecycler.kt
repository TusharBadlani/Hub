package com.example.experiment.epoxy;

import android.view.View
import cessini.technology.commonui.HomeGrid.ChildRecyclerController
import cessini.technology.commonui.R
import com.airbnb.epoxy.*


@EpoxyModelClass
abstract class StoryModelRecycler : EpoxyModelWithHolder<StoryModelRecycler.Holder>() {
    @EpoxyAttribute
    lateinit var controller: ChildRecyclerController

    override fun bind(holder: Holder) {
        holder.recyclerView.setController(controller)
    }

    override fun getDefaultLayout(): Int {
        return R.layout.item_story_epoxy_recycler
    }

    class Holder : EpoxyHolder() {
        lateinit var recyclerView: EpoxyRecyclerView
        override fun bindView(itemView: View) {
            recyclerView = itemView.findViewById(R.id.recyclerViewChild)
        }
    }
}
