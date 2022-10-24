package cessini.technology.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.presentation.common.navigateToProfile
import cessini.technology.commonui.presentation.globalviewmodels.BaseViewModel
import cessini.technology.model.Notification
import cessini.technology.newapi.model.MyWorldNotification
import cessini.technology.newapi.preferences.AuthPreferences
import cessini.technology.newrepository.explore.ExploreRepository
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import cessini.technology.notifications.databinding.FragmentNotficationTabBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotficationTabFragment : Fragment() {

    companion object {
        private const val TAG = "NotficationTab"
    }

    @Inject
    lateinit var exploreRepository: ExploreRepository

    private val baseViewModel: BaseViewModel by activityViewModels()

    private var _binding: FragmentNotficationTabBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userIdentifierPreferences: UserIdentifierPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notfication_tab,
            container,
            false
        )
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val authPreferences = AuthPreferences(requireContext())
        userIdentifierPreferences = UserIdentifierPreferences(requireContext(), authPreferences)
        buildNotificationModelsOrDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildNotificationModelsOrDismiss() {
        val notification = ArrayList<MyWorldNotification>()
        viewLifecycleOwner.lifecycleScope.launch {

            Log.d(TAG, "firebase: ${userIdentifierPreferences.firebaseToken}")
            if (userIdentifierPreferences.loggedIn) {
                val res = exploreRepository.getNotifications().data.myWorldNotifications
                res.forEach {
                    notification.add(it)
                }
            } else {
                exploreRepository.getNotificationPlease()
                    .body()?.data?.myWorldNotifications?.forEach {
                        notification.add(it)
                    }
            }

//            if (notification.isEmpty()) {
//                binding.tvMsgNoNotification2.isVisible = true
//            }

//            if(notification.isNotEmpty()){
//                binding.noNotificationHelpText2.visibility = View.GONE
//            }
//
            buildNotificationsModel(notification)
        }
    }

//    private suspend fun getNotifications(token: String): ApiNotification {
//        return  exploreRepository.getNotifications("")
//
//    }

    private fun buildNotificationsModel(notifications: List<MyWorldNotification>) {
        binding.notifications2.withModels {
            notifications.forEachIndexed { index, notification ->
                listNotificationNew {
                    id(index)
                    content(notification.message)
                    image(notification.profile_image)
                    time(5L)
                    title(notification.username)
                    onClick { _ -> Unit }
                }
            }
        }
    }


    private fun handleNotificationClick(notification: Notification) {
        findNavController().navigateUp()
        when (notification.type) {
            Notification.Type.FOLLOW -> navigateToProfile(
                notification.id,
                baseViewModel.id.value.orEmpty()
            )
            else -> Unit
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPauseCalled")
    }
}