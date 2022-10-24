package cessini.technology.commonui.presentation.auth

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cessini.technology.commonui.R
import cessini.technology.commonui.presentation.HomeActivity
import cessini.technology.commonui.databinding.FragmentSignInBinding
import cessini.technology.commonui.presentation.globalviewmodels.BaseViewModel
import cessini.technology.commonui.presentation.auth.SignInViewModel
import cessini.technology.navigation.NavigationFlow
import cessini.technology.navigation.ToFlowNavigable
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SignInFragment : BottomSheetDialogFragment() {
    companion object {
        private const val TAG = "SignInActivity"
        private const val EMAIL = "email"
    }

    private var _binding: FragmentSignInBinding? = null
    val binding get() = _binding!!

    private val signInViewModel: SignInViewModel by viewModels()
    private val baseViewModel: BaseViewModel by activityViewModels()

    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var callbackManager: CallbackManager


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setLightNavigationBar(dialog)
        return dialog

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)

        Log.d("CheckNav", "SignInFragment, ${activity?.localClassName}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        callbackManager = CallbackManager.Factory.create()

        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )
        binding.scontext = this
        binding.sigin = signInViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        /*
         * To prevent cancelling of the fragment by clicking anywhere outside the bottom sheet
         */
        this.isCancelable = false
        binding.roomNoticeDropdownIcon.setOnClickListener {
            dismiss()
        }

        binding.textView8.setOnClickListener {
            dismiss()
        }

        //google auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(client_id)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        //google auth ends
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        //google auth
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val authFlagSet = (activity as HomeActivity).baseViewModel.authFlag

        authFlagSet.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }

        Log.d("SECRET", client_id)
        //google auth ends
    }

    override fun onResume() {
        super.onResume()

        //google auth
        binding.signInButton.setOnClickListener {
            binding.signInButton.visibility = View.INVISIBLE
            binding.btnProgress.isVisible = true
            googleSignIn()
        }

        binding.facebookSignIn.setOnClickListener {
            binding.facebookSignIn.visibility = View.INVISIBLE
            binding.btnProgressFacebook.isVisible = true
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf(EMAIL, "public_profile"))
            facebookSignIn()
        }

        Log.d("SignInFrag", "Google Auth Listener Set Up")
        binding.termsAndCondition.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndCondition.setLinkTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.cpDark
            )
        )

        //google auth ends
    }

    private val googleSignInContract = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        binding.signInButton.visibility = View.VISIBLE
        binding.btnProgress.isVisible = false
        handleSignInResult(task)
    }

    private fun googleSignIn() {
        Log.d("SignInFrag", "Google Sign In Called")
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInContract.launch(signInIntent)
    }

    private fun facebookSignIn() {
        Log.d("SignInFrag", "Facebook sign in Called")
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken
                    signInViewModel.facebookSignIn(token, { authEntity, profile ->
                        signInViewModel.insertDataInDB(authEntity) {
                            if (it.isBlank()) {
                                //first time login
                                goToEditProfile()
                            } else {
                                //subsequent login
                                goBack()
                            }
                        }
                    }, {
                        baseViewModel.nukeUserAtSignOut()
                    })
                }

                override fun onCancel() {
                    Toast.makeText(context, "Facebook login was cancelled", Toast.LENGTH_SHORT)
                    binding.btnProgressFacebook.visibility = View.GONE
                    binding.facebookSignIn.visibility = View.VISIBLE
                }

                override fun onError(error: FacebookException) {
                    Log.e(
                        "Error",
                        "An error has occured in login and it is " + error.printStackTrace()
                    )
                    binding.btnProgressFacebook.visibility = View.GONE
                    binding.facebookSignIn.visibility = View.VISIBLE
                }
            })
    }

    fun goToEditProfile() {
        Log.d(TAG, "goToEditProfile")
        baseViewModel.setAuthFlag(true)
        (activity as ToFlowNavigable).navigateToFlow(
            NavigationFlow.GlobalEditProfileFlow
        )
    }

    fun goBack() {
//        (activity as ToFlowNavigable).navigateToFlow(
//            NavigationFlow.HomeFlow
//        )
        Log.d(TAG, "goBack to sign in fragment from email list: making authFlag true")
        baseViewModel.setAuthFlag(true)
//        activity.findNavController(R.id.nav_host_fragment).navigateUp()

    }

    private fun setLightNavigationBar(dialog: Dialog){
        val insetsController = WindowCompat.getInsetsController(
            dialog.window!!,
            requireActivity().findViewById(android.R.id.content)
        )
        insetsController.isAppearanceLightNavigationBars = true
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            Log.d("SignInFrag", "Google Sign In Successful IDTOKEN: ${account?.idToken}")
            baseViewModel.setGoogleAuthFlag(true)
            signInViewModel.signIn(account!!, { authEntity, profile ->
                signInViewModel.insertDataInDB(authEntity) {
                    if (it == "") {
                        //first time login
                        goToEditProfile()
                    } else {
                        //subsequent login
                        goBack()
                    }
                }
            }, {
                baseViewModel.nukeUserAtSignOut()
            })
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode, e)
//            Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        if ((activity as HomeActivity).baseViewModel.googleAuthFlag.value == false &&
            findNavController().currentDestination?.label.toString().lowercase() != "homefragment"
        ) {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private const val client_id =
    "909136434512-cca9i6u7cjo19l8583l6ho5q6c5h7ccf.apps.googleusercontent.com"
