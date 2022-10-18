package cessini.technology.camera.domain.adapter.cameraModesAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cessini.technology.camera.databinding.CameraModesRecyclerItemsBinding
import cessini.technology.camera.presentation.fragment.CameraFragment
import cessini.technology.cvo.cameraModels.CameraModes

class CameraModesAdapter(
    private var listOfModes: ArrayList<CameraModes>,
    val frag: CameraFragment
) :
    RecyclerView.Adapter<CameraModesAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "CamModesAdapter"
    }

    class ViewHolder(val binding: CameraModesRecyclerItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**Binding the data of the song into the view.*/
        fun bind(cameraModes: CameraModes) {
            binding.cameraModeName.text = cameraModes.mode
        }

//        /**Perform actions based on the clicks in the view. */
//        fun onClick(cameraModes: CameraModes) {
//
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /** Setting up the view for the recycler view and then returning the view. */
        val view = CameraModesRecyclerItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /**Binding the Data of the Current Camera Mode.*/

        holder.bind(listOfModes[position])

//        /**Perform Actions according to the Click in the view. */
//        holder.onClick(listOfModes[position])
    }

    override fun getItemCount(): Int {
        return listOfModes.size
    }


}