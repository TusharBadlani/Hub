package cessini.technology.commonui.presentation.common

import android.content.res.Resources
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import cessini.technology.commonui.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.ceil
import kotlin.math.floor


abstract class BaseBottomSheet<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
) : BottomSheetDialogFragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!
    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
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

    fun setLevel(level:Int){
        val margins = -1*(level) * 9
        val width = (0.05)*-(1*(level))
        addMargin(binding.root,margins,(1-width).toFloat())
    }
    fun setMotionLevel(level:Float) {
        val margins = -1*(floor(level).toInt())*9
        val width = (0.05)*-(1*(level))
        addMargin(binding.root, margins,(1-width).toFloat())
    }
    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }
    private fun addMargin(view: View,margins:Int,width:Float) {

        val layoutParams: FrameLayout.LayoutParams =
            view.layoutParams as FrameLayout.LayoutParams
        val margin = margins.toPx().toInt()
        layoutParams.setMargins(margin, 0, margin, 0)

        view.layoutParams = layoutParams
        view.requestLayout()

        val displayRectangle = Rect()
        this.requireDialog().window!!
            .setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.cpTransparent)))
        val window: Window = requireActivity().window
        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)
        this.requireDialog().window!!.setLayout(
            if(width==1F) -1 else (displayRectangle.width() * width).toInt(),
            -1
        )
    }

    fun Number.toPx() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

    fun setUpFullScreen(dialog: BottomSheetDialog, dp: Int) {
        val bottomSheet = dialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
        val behaviour = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if(layoutParams != null) {
            val height = ((windowHeight * 75) / 2340) + (dp - 75)
            layoutParams.height =
                windowHeight - height.toPx().toInt() - 15
        }
        bottomSheet.layoutParams = layoutParams
        behaviour.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMatrix = DisplayMetrics()
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = requireActivity().display
            display?.getRealMetrics(displayMatrix)
            displayMatrix.heightPixels
        }else{
            @Suppress("DEPRECATION")
            val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(displayMatrix)
            displayMatrix.heightPixels
        }
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
