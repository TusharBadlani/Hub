package cessini.technology.myspace.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@HiltViewModel
//class PublicProfileRoomMoreInformationFragmentViewModel @Inject :ViewModel() {
//    companion object {
//        private const val TAG = "PublicProfileViewModel"
//    }
//    val moreInfoBottomSheetDraggedState = MutableLiveData<Float>()
//
//    fun updateMoreInfoDraggedState(state:Float) {
//        moreInfoBottomSheetDraggedState.value = state
//    }
//}
@HiltViewModel
class PublicProfileRoomMoreInformationFragmentViewModel @Inject constructor():ViewModel() {
    companion object {
        private const val TAG = "PublicProfileViewModel"
    }
    val moreInfoBottomSheetDraggedState = MutableLiveData<Float>()

    fun updateMoreInfoDraggedState(state:Float) {
        moreInfoBottomSheetDraggedState.value = state
    }
}