package cessini.technology.camera.utils.diffutil

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import cessini.technology.model.SongInfo


class SongAdapterDiffUtilCallback(newList: ArrayList<SongInfo>?, oldList: ArrayList<SongInfo>?) :
    DiffUtil.Callback() {

    var newList: ArrayList<SongInfo>? = newList
    var oldList: ArrayList<SongInfo>? = oldList

    override fun getOldListSize(): Int {
        return if (oldList != null) oldList!!.size else 0
    }

    override fun getNewListSize(): Int {
        return if (newList != null) newList!!.size else 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList!![newItemPosition].upload_file === oldList!![oldItemPosition].upload_file
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val result: Int = newList!![newItemPosition].compareTo(oldList!![oldItemPosition])
        return result == 0
    }

    @Nullable
    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val newModel = newList!![newItemPosition]
        val oldModel = oldList!![oldItemPosition]
        val diff = Bundle()
        if (newModel.title !== oldModel.title) {
            diff.putString("title", newModel.title)
        }
        return if (diff.size() == 0) {
            null
        } else diff
    }

}
