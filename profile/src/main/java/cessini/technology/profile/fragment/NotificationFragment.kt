package cessini.technology.profile.fragment
//
//import android.app.Dialog
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import cessini.technology.commonui.common.BaseBottomSheet
//import cessini.technology.commonui.common.navigateToProfile
//import cessini.technology.commonui.utils.Constant
//import cessini.technology.commonui.utils.Constant.Companion
//import cessini.technology.model.Notification
//import cessini.technology.model.Notification.Type.*
//import cessini.technology.newrepository.explore.ExploreRepository
//import cessini.technology.profile.R
//import cessini.technology.profile.databinding.FragmentNotificationBinding
//import cessini.technology.profile.listItemNotification
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.google.android.material.bottomsheet.BottomSheetDialog
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@AndroidEntryPoint
//internal class NotificationFragment :
//    BaseBottomSheet<FragmentNotificationBinding>(R.layout.fragment_notification) {
//
//    @Inject
//    lateinit var exploreRepository: ExploreRepository
//
//    var count = 0
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.backNavigationButtonDiscoveryProfile.setOnClickListener {
//            findNavController().navigateUp()
//        }
//
//        buildNotificationModelsOrDismiss()
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.setOnShowListener {
//            val bottomSheetDialog = it as BottomSheetDialog
//            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight)
//        }
//        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                if(newState == BottomSheetBehavior.STATE_SETTLING) {
//                    count++
//                    if(count % 2 == 0){
//                        dialog.dismiss()
//                    }
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
//
//        })
//        dialog.behavior.isDraggable = false
//        return dialog
//    }
//
//    private fun buildNotificationModelsOrDismiss() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            val notifications = getNotifications()
//
//            if (notifications.isEmpty()) {
//                binding.tvMsgNoNotification.isVisible = true
//            }
//
//            if(notifications.isNotEmpty()){
//                binding.noNotificationHelpText.visibility = View.GONE
//            }
//
//            buildNotificationsModel(notifications)
//        }
//    }
//
//    private suspend fun getNotifications(): List<Notification> {
//        return runCatching { exploreRepository.getNotifications() }
//            .getOrDefault(emptyList())
//    }
//
//    private fun buildNotificationsModel(notifications: List<Notification>) {
//        binding.notifications.withModels {
//            notifications.forEachIndexed { index, notification ->
//                listItemNotification {
//                    id(index)
//                    content(notification.message)
//                    image(notification.image)
//                    time(notification.time)
//                    onClick { _ -> handleNotificationClick(notification) }
//                }
//            }
//        }
//    }
//
//    private fun handleNotificationClick(notification: Notification) {
//        findNavController().navigateUp()
//
//        when (notification.type) {
//            ERROR -> Unit
//            VIDEO_LIKE -> Unit
//            STORY_LIKE -> Unit
//            VIDEO_COMMENT -> Unit
//            STORY_COMMENT -> Unit
//            FOLLOW -> navigateToProfile(notification.id)
//            ROOM_JOIN_REQUEST -> findNavController().navigate(R.id.to_manage_room)
//            ROOM_JOIN_REQUEST_ACCEPTED -> Unit
//            VIDEO_UPLOAD -> Unit
//            CREATE_ROOM_REQUEST -> Unit
//        }
//    }
//}
