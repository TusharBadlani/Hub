package cessini.technology.profile.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.presentation.common.navigateToProfile
import cessini.technology.commonui.presentation.globalviewmodels.BaseViewModel
import cessini.technology.model.RequestProfile
import cessini.technology.profile.R
import cessini.technology.profile.databinding.FragmentRoomRequestBinding
import cessini.technology.profile.listItemRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UserRoomRequest : BaseFragment<FragmentRoomRequestBinding>(R.layout.fragment_room_request) {

    private val pendingRequests = MutableStateFlow(emptyList<RequestProfile>())
    private val baseViewModel by activityViewModels<BaseViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        collectPendingRequest()
    }

    private fun collectPendingRequest() {
        pendingRequests
            .filter { it.isNotEmpty() }
            .onEach { buildModel(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun setupUi() {
        pendingRequests.onEach {
            with(binding) {
                requestLabel.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                requestText.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun buildModel(pendingRequests: List<RequestProfile>) {
        binding.requests.withModels {
            pendingRequests.onEach {
                listItemRequest {
                    reqUserName(it.name)
                    reqUserChannel(it.channel)
                    reqUserImage(it.picture)
                    onReqProfileClick { _ ->
                        navigateToProfile(
                            it.id,
                            baseViewModel.id.value.orEmpty()
                        )
                    }
                    onAccept { _ -> }
                }
            }
        }
    }
}
