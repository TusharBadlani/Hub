package cessini.technology.commonui.presentation.appintro

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.databinding.FragmentIntroductionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroductionFragment : BaseFragment<FragmentIntroductionBinding>(R.layout.fragment_introduction) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        systemBarInsetsEnabled = false
        binding.btnNext.setOnClickListener { onClickNext() }

        Log.e("CheckNav","IntroductionFragment, ${activity?.localClassName}")
    }

    private fun onClickNext() {
        findNavController().navigate(
            IntroductionFragmentDirections.actionIntroductionToCategorySelection()
        )
    }
}
