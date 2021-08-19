package com.carkzis.android.silenus.user

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentLoginBinding
import com.carkzis.android.silenus.showToast
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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
    ): View? {

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

    private fun setUpNavigationToWelcomeFragment() {
        viewModel.navToWelcome.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
                    )
                }
        })
    }

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