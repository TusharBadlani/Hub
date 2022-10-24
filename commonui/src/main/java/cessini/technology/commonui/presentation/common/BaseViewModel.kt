package cessini.technology.commonui.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {
    private val _events = Channel<T>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    protected fun T.send() {
        viewModelScope.launch {
            _events.send(this@send)
        }
    }
}
