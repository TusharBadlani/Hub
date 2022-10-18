package cessini.technology.commonui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
) : Fragment() {

    private var _binding: T? = null
    /* TODO: This should be made protected. Blocked by several usages.
        Re-write fragments to not directly allow access to binding
        and instead delegate access via callbacks */
    val binding get() = _binding!!

    // TODO: This should be made final. Blocked by an override in VideoUploadFragment.kt
    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate<T>(
            inflater,
            layoutId,
            container,
            false,
        ).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyInsets()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Whether system bar insets are applied to this fragment's root view.
     * Enabled by default.
     * This should be set to false when a fragment needs to draw under the status bar; inorder to
     * opt out of applying windows's system bar insets.
     */
    protected var systemBarInsetsEnabled: Boolean = true
    set(value) {
        field = value
        // make sure we request OnApplyWindowInsets dispatch each time this value is changed
        view?.let { ViewCompat.requestApplyInsets(it) }
    }

    private fun applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(requireView()) { view, insets ->
            if (systemBarInsetsEnabled) {
                val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMargins(top = systemBarInsets.top)
                }
            }

            insets
        }
    }
}
