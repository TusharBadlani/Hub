package cessini.technology.commonui.presentation.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

inline fun <T> ViewModel.request(
    requestInProgress: MutableLiveData<Boolean>,
    crossinline block: suspend () -> T,
    crossinline onSuccess: suspend (T) -> Unit,
    crossinline onFailure: suspend (Throwable) -> Unit,
) {
    if (requestInProgress.value == true) return
    requestInProgress.value = true

    viewModelScope.launch {
        runCatching {
            block()
        }.onSuccess {
            requestInProgress.value = false
            onSuccess(it)
        }.onFailure {
            requestInProgress.value = false
            onFailure(it)
        }
    }
}
