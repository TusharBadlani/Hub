package cessini.technology.commonui.presentation.suggestion.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.common.BaseFragment
import cessini.technology.commonui.databinding.FragmentSuggestionBinding
import cessini.technology.commonui.epoxy.suggestion.SubCategoryViewHolderController
import cessini.technology.commonui.presentation.suggestion.SuggestionViewModel
import cessini.technology.model.ListCategory
import cessini.technology.model.Subcategory
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class SuggestionFragment : BaseFragment<FragmentSuggestionBinding>(R.layout.fragment_suggestion) {
    companion object {
        private const val TAG = "SuggestionFragment"
    }

    private val viewModel: SuggestionViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.isEnabled = false
        binding.btnNext.setOnClickListener { onClickNext() }

        Log.e("CheckNav","SuggestionFragment, ${activity?.localClassName}")

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categories.collectLatest { category ->
                    if (category == null) {
                        binding.progressBar.isVisible = true
                        return@collectLatest
                    }

                    launch(Dispatchers.Default) {
                        // TODO: This logic must be moved to the controller
                        val subCategoryList = category.map { category ->

                            val categoryTitle = category.key
                            val subcategories = category.value

                            // Evenly divide chips into three chip groups
                            val chunkSize = subcategories.size / 3 +
                                    if ((subcategories.size % 3) != 0) 1 else 0

                            val groupOne = ChipGroup(requireContext())
                            val groupTwo = ChipGroup(requireContext())
                            val groupThree = ChipGroup(requireContext())

                            subcategories.forEachIndexed { i, subcategory ->
                                val destination = when {
                                    i < chunkSize -> groupOne
                                    i >= chunkSize && i < chunkSize * 2 -> groupTwo
                                    else -> groupThree
                                }

                                withContext(Dispatchers.Main) {
                                    destination.addView(buildSubcategoryChip(subcategory))
                                }
                            }

                            ListCategory(
                                categoryTitle,
                                groupOne,
                                groupTwo,
                                groupThree
                            )
                        }

                        withContext(Dispatchers.Main) {
                            submitCategoriesChipsToController(subCategoryList)
                            binding.progressBar.isGone = true
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            // Next button must be enabled only when 5 or more items are selected
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedSubCategories
                    .map { it.size >= 5 }
                    .collectLatest {
                        binding.btnNext.isEnabled = it
                    }
            }
        }
    }

    private fun submitCategoriesChipsToController(subCategoryList: List<ListCategory>) {
        binding.epoxySuggestion.adapter = viewHolderController.adapter
        viewHolderController.setData(subCategoryList)
        viewHolderController.setFilterDuplicates(true)
    }

    private val viewHolderController by lazy {
        SubCategoryViewHolderController(requireContext())
    }

    private fun buildSubcategoryChip(subcategory: Subcategory): Chip {
        val chip = Chip(requireContext())
        val chipDrawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            R.style.MyworldChipStyle
        )
        chip.setChipDrawable(chipDrawable)
        chip.setTextAppearance(R.style.MyworldChipStyle)
        chip.text = subcategory.subCategory
        chip.isCheckable = true

        // Initial state from viewModel
        chip.isChecked = subcategory in viewModel.selectedSubCategories.value

        chip.setOnClickListener {
            if (chip.isChecked) {
                viewModel.addToSelection(subcategory)
            } else {
                viewModel.removeFromSelection(subcategory)
            }
        }

        return chip
    }

    private fun onClickNext() {
        if (viewModel.selectedSubCategories.value.size <= 4) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.finalizeCategories()
                findNavController().navigate(
                    SuggestionFragmentDirections.actionCategorySelectionToLanguageSelection()
                )
            } catch (e: Exception) {
                Log.e(TAG, "Unable to finalize user categories", e)
            }
        }
    }
}
