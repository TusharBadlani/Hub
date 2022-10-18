package cessini.technology.commonui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cessini.technology.commonui.R
import cessini.technology.commonui.adapter.CommentAdapter
import cessini.technology.commonui.databinding.FragmentCommentBinding
import cessini.technology.commonui.viewmodel.BaseViewModel
import cessini.technology.commonui.viewmodel.CommentsViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentFragment : BottomSheetDialogFragment() {


    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    private val adapter: CommentAdapter by lazy { CommentAdapter() }

    private val commentsViewModel by activityViewModels<CommentsViewModel>()
    private val baseViewModel by activityViewModels<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDarkDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDarkDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        (dialog as BottomSheetDialog).behavior.isFitToContents = true
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        commentsViewModel.commentsList.observe(viewLifecycleOwner) { comments ->
            Log.d("Comment", comments.size.toString())
            adapter.submitList(comments)
            binding.rvComments.adapter = adapter
            binding.rvComments.layoutManager = LinearLayoutManager(context)

            if (binding.txtNoCommentsRightNow.visibility == View.VISIBLE && comments.size > 0) {
                binding.txtNoCommentsRightNow.visibility = View.GONE
            }

        }

        binding.txtPostComment.setOnClickListener {
            if (baseViewModel.authFlag.value == false) {
                goToAuth()
                return@setOnClickListener
            }

            if (binding.etCommentBody.text != null && binding.etCommentBody.text.trim() != "") {
                commentsViewModel.postCommentVideo(
                    binding.etCommentBody.text.toString(),
                )
                binding.etCommentBody.text.clear()
            }
        }
    }

    fun goToAuth() {
        (requireActivity() as ToFlowNavigable).navigateToFlow(
            NavigationFlow.AuthFlow
        )
    }

    override fun onStop() {
        super.onStop()
        commentsViewModel.commentsList.value?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
