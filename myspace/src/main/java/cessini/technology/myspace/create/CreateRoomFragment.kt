package cessini.technology.myspace.create

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import cessini.technology.commonui.presentation.auth.SignInFragment
import cessini.technology.commonui.presentation.common.BaseBottomSheet
import cessini.technology.commonui.presentation.common.toast
import cessini.technology.commonui.utils.Constant.Companion.settingBottomSheetHeight
import cessini.technology.commonui.presentation.common.BottomSheetLevelInterface
import cessini.technology.myspace.R
import cessini.technology.myspace.create.CreateRoomSharedViewModel.Event.*
import cessini.technology.myspace.databinding.FragmentCreateRoomBinding
import cessini.technology.newrepository.myworld.ProfileRepository
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_room.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateRoomFragment(private val listener: BottomSheetLevelInterface?) :
    BaseBottomSheet<FragmentCreateRoomBinding>(R.layout.fragment_create_room),
    BottomSheetLevelInterface {

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val shared_pref_name_for_know_more: String = "know_more"
    private val know_more_variable: String = "isKnowMoreOpened"

    private lateinit var preference: SharedPreferences


    private val roomSharedViewModel: CreateRoomSharedViewModel by activityViewModels()
    private val publicProfileRoomMoreInformationFragmentViewModel:PublicProfileRoomMoreInformationFragmentViewModel by activityViewModels()

    var count = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preference = requireContext().getSharedPreferences(
            shared_pref_name_for_know_more,
            MODE_PRIVATE
        )
        roomSharedViewModel.isLoggedIn()
//        if(roomSharedViewModel.loggedIn.value==false){
//            goToAuth()
//        }

        with(binding) {
            viewModel = roomSharedViewModel
            btnSchedule.setOnClickListener(navToSchedule)
            textView27.setOnClickListener{
                val bottomSheet = MySpaceAreaFragment(this@CreateRoomFragment)
                bottomSheet.show(parentFragmentManager,bottomSheet.tag)
            }
            button2.setOnClickListener{

                button2.isSelected = !button2.isSelected
                if(it.isSelected) {
                    it.setBackground(resources.getDrawable(R.drawable.button_golive))
                    button2.setTextColor(Color.parseColor("#FFFFFF"))
                }
                else{
                    it.setBackground(resources.getDrawable(R.drawable.round_viewbutton))
                    button2.setTextColor(Color.parseColor("#ff0f1419"))
                }
            }
            //linearButton.setOnClickListener(navToSuggestion)
            linearButton.setOnClickListener {
//                btn_progress.isVisible=true
//                Log.e("MySpaceFrag","btnProgress called")
                val bottomSheet = MySpaceAreaFragment(this@CreateRoomFragment)
                bottomSheet.show(parentFragmentManager,bottomSheet.tag)
//                Log.e("MySpaceFrag","navToSuggestion")
            }
        }

//        binding.constraintLayout3.progress = 0f
//
//        createRoomViewModel.bottomSheetDraggedState.observe(viewLifecycleOwner) { state->
//            val shiftedState = (1+state)/2f;
//            setMotionLevel(-shiftedState)
//            binding.constraintLayout3.progress = shiftedState
//        }
        binding.constraintLayout3.progress = 0f

        publicProfileRoomMoreInformationFragmentViewModel.moreInfoBottomSheetDraggedState.observe(viewLifecycleOwner) { state->
            val shiftedState = (1+state)/2f
            setMotionLevel(-shiftedState)

        }


        returnHeightWhenReady()

        //setUpKnowMoreButton()
        setUpInfoButton()

//        observeTime()
        listenEvents()
        //setUpRoomCategory()

        //setUpGoLive()
        setUpCreateHub()

        setUpImage()

    }

    private fun setUpImage() {
        if(roomSharedViewModel.loggedIn.value==true)
        lifecycleScope.launch {
            profileRepository.profile.collectLatest {
                binding.image=it.profilePicture
            }
        }
        else{
            binding.image = resources.getDrawable(R.drawable.ic_user_defolt_avator).toString()
        }
    }

    private fun setUpCreateHub(){
        binding.btnNext.setOnClickListener {
            if(roomSharedViewModel.loggedIn.value==false){
                goToAuth()
            }
            else {
                if (binding.button2.isSelected) {
                    if (editTextSearch.text.isNullOrEmpty() || editTextSearch.text.isNullOrBlank() || editTextSearch.text.length == 0) {
                        toast(message = "Enter name of Live Room")
                    } else {
                        val intent: Intent = Intent()

                        intent.setClassName(
                            requireContext(),
                            "cessini.technology.commonui.presentation.GridActivity"
                        )
//            intent.setClassName(requireContext(),"cessini.technology.myspace.live.LiveMyspaceActivity")
                        intent.putExtra("Room Name", "${editTextSearch.text.toString().trim()}_room")
                        startActivity(intent)
                    }
                } else {
                    roomSharedViewModel.createRoom()
                }
            }
        }
    }

//    private fun setUpGoLive() {
//        binding.button2.setOnClickListener {
//            roomSharedViewModel.isLoggedIn()
//            if(roomSharedViewModel.loggedIn.value==true) {
//                binding.button2.setBackground(resources.getDrawable(R.drawable.button_golive))
//
//                val intent: Intent = Intent()
//
//                intent.setClassName(
//                    requireContext(),
//                    "cessini.technology.commonui.presentation.GridActivity"
//                )
////            intent.setClassName(requireContext(),"cessini.technology.myspace.live.LiveMyspaceActivity")
//                intent.putExtra("Room Name", "${editTextSearch.text} room")
//                startActivity(intent)
//            }
//            else{
//                toast(message = "Log in before creating Live Room")
//            }
//        }
//    }

//    private fun setUpRoomCategory() {
//        categories.forEachIndexed { index, videoCategory ->
//            binding.chipGroup
//                .addView(chipCreator(videoCategory.name) { chipChecked ->
//                    with(roomSharedViewModel.selectedRoomCategories) {
//                        if(this.size < 3) {
//                            if (chipChecked) add(index) else remove(index)
//                        } else if(chipChecked) {
//                            toast("Maximum 3 categories are allowed")
//                            (binding.chipGroup.get(index) as Chip).isChecked = false
//                        } else if(!chipChecked) {
//                            remove(index)
//                        }
//                    }
//                })
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setUpFullScreen(bottomSheetDialog, settingBottomSheetHeight + 6)
        }

        (dialog as BottomSheetDialog).behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_SETTLING) {
                    count++
                    if (count % 2 == 0) {
                        dismiss()
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                roomSharedViewModel.updateDraggedState(slideOffset)
            }
        })
        return dialog
    }

    /*
     * This function returns the height of the layout to the
     * ManageRoomFragment for changing the height of thet fragment
     */
    private fun returnHeightWhenReady() {
        binding.createRoomConstraintLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                binding.createRoomConstraintLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                listener?.getHeightOfBottomSheet(binding.createRoomConstraintLayout.height)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

//    private fun setUpKnowMoreButton() {
//        binding.knowMoreTextView.setOnClickListener {
//            if (!preference!!.getBoolean(know_more_variable, false)) {
//                val bottomSheet = PublicProfileRoomMoreInformationFragment(this, initialValue)
//                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
//            }
//        }
//        if(preference.getBoolean(know_more_variable, false)) {
//            binding.knowMoreTextView.visibility = View.GONE
//        }
//    }

    private fun setUpInfoButton() {
        binding.imageView8.setOnClickListener {
            //if (!preference!!.getBoolean(know_more_variable, false)) {
                val bottomSheet = PublicProfileRoomMoreInformationFragment(this)
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
            //}
        }
//        if(preference.getBoolean(know_more_variable, false)) {
//            binding.imageView8.visibility = View.GONE
//        }
    }

    private fun listenEvents() {
        roomSharedViewModel!!.events.onEach {
            when (it) {
                is Failed -> toast(it.reason)
                is RoomCreated -> handleRoomCreatedEvent(it.name)
                is ShowToast -> toast(it.message)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeTime() {
        roomSharedViewModel!!.time.observe(viewLifecycleOwner) {
//            binding { btnSchedule.text = it.dateTime() }
        }
    }

    private fun handleRoomCreatedEvent(roomName: String) {

        toast(message = "\"$roomName\" created :)")
        dismiss()
    }

    fun goToAuth() {
        val fragment = SignInFragment()
        val fragmentManager = parentFragmentManager
        fragment.show(fragmentManager, fragment.tag)

    }

    private val navToSchedule: (View) -> Unit = {

        val bottomSheet = ScheduleFragment(this)
        bottomSheet.show(parentFragmentManager,bottomSheet.tag)
    }

    private val navToSuggestion: (View) -> Unit={
        val bottomSheet = MySpaceAreaFragment(this)
        bottomSheet.show(parentFragmentManager,bottomSheet.tag)
    }

//    private fun chipCreator(title: String, onClick: (checked: Boolean) -> Unit): Chip {
//        return DataBindingUtil.inflate<VideoCategoryChipBinding>(
//            layoutInflater,
//            cessini.technology.commonui.R.layout.video_category_chip,
//            binding.chipGroup,
//            false,
//        ).also { binding ->
//            binding.title = title
//            binding.chip.setOnCheckedChangeListener { _, isChecked -> onClick(isChecked) }
//        }.root as Chip
//    }

    override fun onSheet2Dismissed() {
        setLevel(0)
        listener?.onSheet2Dismissed()
        Log.e("RoomFragment","onSheet2Dismissed")
        binding.btnProgress.isVisible=false
//        if(preference.getBoolean(know_more_variable, false)) {
//            binding.imageView8.visibility = View.GONE
//        }
    }

    override fun onSheet2Created() {
        setLevel(-1)
        listener?.onSheet2Created()
    }

    override fun onSheet1Dismissed() = Unit

    override fun getHeightOfBottomSheet(height: Int) {
        binding.createRoomConstraintLayout.layoutParams.height = height + 10.toPx().toInt()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener?.onSheet1Dismissed()
//        btn_progress.isVisible=false
        roomSharedViewModel.roomTitle.value = ""
        roomSharedViewModel.time.value = 0
        roomSharedViewModel.selectedRoomCategories.clear()
        roomSharedViewModel.categorySet.clear()
        roomSharedViewModel.subCategorySet.clear()
    }

    override fun onPause() {
        super.onPause()
        Log.e("Pauese","OnPauseCalled")
    }

}
//val categories = setOf(
//    VideoCategory(id = "1", name = "Film and animation"),
//    VideoCategory(id = "2", name = "Cars and vehicles"),
//    VideoCategory(id = "3", name = "Music"),
//    VideoCategory(id = "4", name = "Pets and animals"),
//    VideoCategory(id = "5", name = "Sport"),
//    VideoCategory(id = "6", name = "Travel and events"),
//    VideoCategory(id = "7", name = "Gaming"),
//    VideoCategory(id = "8", name = "People and blogs"),
//    VideoCategory(id = "9", name = "Comedy"),
//    VideoCategory(id = "10", name = "Entertainment"),
//    VideoCategory(id = "11", name = "News and politics"),
//    VideoCategory(id = "12", name = "How-to and style"),
//    VideoCategory(id = "13", name = "Education"),
//    VideoCategory(id = "14", name = "Science and technology"),
//    VideoCategory(id = "15", name = "Non-profits and activism"),
//)
