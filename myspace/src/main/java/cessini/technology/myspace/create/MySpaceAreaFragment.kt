package cessini.technology.myspace.create

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.presentation.common.toast
import cessini.technology.commonui.utils.Constant
import cessini.technology.model.ListCategory
import cessini.technology.model.Subcategory
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.myspace.R
import cessini.technology.myspace.databinding.FragmentMySpaceAreaBinding
import cessini.technology.newrepository.explore.RegistrationRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class MySpaceAreaFragment(val levelInterface: BottomSheetLevelInterface) :
    BaseBottomSheet<FragmentMySpaceAreaBinding>(
        R.layout.fragment_my_space_area
    ) {

    companion object {
        private const val TAG = "MySpaceAreaFragment"
    }

    @Inject
    lateinit var registrationRepository: RegistrationRepository

    private val selectedVideoCategories = mutableSetOf<String>()
    private val selectedVideoSubcategories = mutableSetOf<String>()


    val dataTopicList = ArrayList<String>()
    val subCategoryList = ArrayList<ListCategory>()
    val firstList = ArrayList<ChipGroup>()
    val secondList = ArrayList<ChipGroup>()
    val thirdList = ArrayList<ChipGroup>()

    val map: Map<String, ArrayList<ArrayList<Chip>>> = HashMap()
    val chipMap = mutableMapOf<String, ArrayList<ArrayList<Chip>>>()

    var count = 0

    private val roomSharedViewModel: CreateRoomSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        levelInterface.onSheet2Created()
        roomSharedViewModel.addData()
        binding.btnProgress.visibility = View.VISIBLE

//        addTopicsToList()
//        setUpEpoxyController()

        (view.parent as View).setBackgroundColor(Color.TRANSPARENT)
        with(binding) {
            backButton.setOnClickListener {
                navigateToRoom()
            }

            doneButton.setOnClickListener {
                if (selectedVideoCategories.size < 4) {
                    if (selectedVideoCategories.size != 0)
                        roomSharedViewModel.categorySet =
                            selectedVideoCategories.toSet() as MutableSet<String>
                    navigateToRoom()
                } else {
                    toast("Maximum 3 categories are allowed")
                }
            }

        }

        roomSharedViewModel.data.observe(viewLifecycleOwner, Observer {
            it.forEach {
//                     Log.d(TAG,"it is $it")
                dataTopicList.add(it.key)
                val len = it.value.size
                val listOne = ChipGroup(requireContext())
                val listTwo = ChipGroup(requireContext())
                val listThree = ChipGroup(requireContext())
                val itemList = ArrayList<Subcategory>()
                itemList.sortByDescending { it.subCategory.length }
                it.value.forEachIndexed { i, subcategory ->
                    itemList.add(subcategory)
                    if (i < len / 3) {
                        listOne.addView(
                            chipCreatorTemp(
                                subcategory.subCategory,
                                subcategory.categoryID,
                                subcategory.id
                            )
                        )
                    } else if (i >= len / 3 && i <= (2 * len) / 3) {
                        listTwo.addView(
                            chipCreatorTemp(
                                subcategory.subCategory,
                                subcategory.categoryID,
                                subcategory.id
                            )
                        )
                    } else {
                        listThree.addView(
                            chipCreatorTemp(
                                subcategory.subCategory,
                                subcategory.categoryID,
                                subcategory.id
                            )
                        )
                    }

                }
                firstList.add(listOne)
                secondList.add(listTwo)
                thirdList.add(listThree)
                subCategoryList.add(ListCategory(it.key, listOne, listTwo, listThree))
            }
            setUpEpoxyController()
            binding.btnProgress.visibility = View.GONE
        })

    }

    private fun setUpEpoxyController() {
        binding.epoxySuggestion.adapter = viewHolderController.adapter
        viewHolderController.setData(subCategoryList)
        viewHolderController.setFilterDuplicates(true)
    }

    private val viewHolderController by lazy {
        SubCategoryViewHolder(requireContext())
    }


    private fun addTopicsToList() {

        lifecycleScope.launchWhenCreated {
            launch(Dispatchers.Default) {
                val gg = registrationRepository.getVideoCategories().data.forEach {
//                     Log.d(TAG,"it is $it")
                    dataTopicList.add(it.key)
                    val len = it.value.size
                    val listOne = ChipGroup(requireContext())
                    val listTwo = ChipGroup(requireContext())
                    val listThree = ChipGroup(requireContext())
                    val itemList = ArrayList<Subcategory>()
                    it.value.forEachIndexed { i, subcategory ->
                        itemList.add(subcategory)
                        if (i <= len / 3) {
                            listOne.addView(
                                chipCreatorTemp(
                                    subcategory.subCategory,
                                    subcategory.categoryID,
                                    subcategory.id
                                )
                            )
                        } else if (i > len / 3 && i <= (2 * len) / 3) {
                            listTwo.addView(
                                chipCreatorTemp(
                                    subcategory.subCategory,
                                    subcategory.categoryID,
                                    subcategory.id
                                )
                            )
                        } else {
                            listThree.addView(
                                chipCreatorTemp(
                                    subcategory.subCategory,
                                    subcategory.categoryID,
                                    subcategory.id
                                )
                            )
                        }

                    }
                    firstList.add(listOne)
                    secondList.add(listTwo)
                    thirdList.add(listThree)
                    subCategoryList.add(ListCategory(it.key, listOne, listTwo, listThree))
                    //Log.d(TAG,"firstlist size for ${it.key}is ${firstList.size}")


                }
                // Log.d(TAG,"chipmap is $chipMap")
            }
        }

    }


    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, Constant.settingBottomSheetHeight + 12)
        }

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 == 0) {
                        dismiss()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })
        dialog.behavior.isDraggable = false
        return dialog
    }

    private fun navigateToRoom() {
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        levelInterface.onSheet2Dismissed()
    }

    private fun chipCreatorTemp(title: String, categoryId: String, subCategoryId: String): Chip {
        //val chip = layoutInflater.inflate(R.layout.single_chip_layout, null,false) as Chip
        val chip = Chip(requireContext())
        var chipDrawable =
            ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.MyworldChipStyle)
        chip.setChipDrawable(chipDrawable)
        chip.setTextAppearance(R.style.MyworldChipStyle)
        chip.text = title
        chip.isCheckable = true

        chip.setOnClickListener {
            if (chip.isChecked) {
                if (selectedVideoCategories.size == 3 && !selectedVideoCategories.contains(
                        categoryId
                    )
                ) {
                    Toast.makeText(
                        requireContext(),
                        "Atmost 3 categories can be selected",
                        Toast.LENGTH_SHORT
                    ).show()
                    chip.isChecked = false
                } else {
                    selectedVideoCategories.add(categoryId)
                    selectedVideoSubcategories.add(subCategoryId)
                }

            }
            if (!chip.isChecked) {
                selectedVideoCategories.remove(categoryId)
                selectedVideoSubcategories.remove(subCategoryId)
            }


//            if(selectedVideoSubcategories.size>= 5){
//                enableParentFragmentNextButton(true)
//            }else enableParentFragmentNextButton(false)

//            Log.d(TAG,"subcategory list is: $selectedVideoCategories")

        }


        return chip
    }
}