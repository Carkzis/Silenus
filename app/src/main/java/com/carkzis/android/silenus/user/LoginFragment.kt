package com.carkzis.android.silenus.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentLoginBinding
import com.carkzis.android.silenus.utils.showToast
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding: FragmentLoginBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()) { res ->
            viewModel.onSignInResult(res)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
         Set up data binding between the fragment and the layout. The lifecycleOwner observes
         the changes in LiveData in this databinding.
         */
        viewDataBinding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            loginViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLoginButton()
        setUpToast()
        setUpNavigationToWelcomeFragment()

    }

    override fun onStart() {
        super.onStart()
        viewModel.authoriseUser()
    }

    /**
     * Navigates the user to the WelcomeFragment if the observed Event holds a true value.
     */
    private fun setUpNavigationToWelcomeFragment() {
        viewModel.navToWelcome.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
                    )
                }
        })
    }

    /**
     * Sets up the layout and intent builders activated on clicking the login button.
     * This will direct the user to the FirebaseAuth login screens.
     */
    private fun setUpLoginButton() {
        viewDataBinding.loginButton.setOnClickListener {
            val customLayout = AuthMethodPickerLayout
                .Builder(R.layout.firebase_auth)
                .setGoogleButtonId(R.id.google_signin)
                .setEmailButtonId(R.id.email_signin)
                .build();
            val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.Theme_Silenus)
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(listOf(
                    AuthUI.IdpConfig.EmailBuilder().build(),
                    AuthUI.IdpConfig.GoogleBuilder().build(),
                ))
                .build()
            signInLauncher.launch(intent)
        }
    }

    /**
     * Sets up the ability to show a toast once by observing the LiveData in either the
     * LoginViewModel or the SharedViewModel.
     */
    private fun setUpToast() {
        viewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(getString(message))
            }
        })
        sharedViewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(message)
            }
        })
    }

}