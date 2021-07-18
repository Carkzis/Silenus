package com.carkzis.android.silenus

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.databinding.FragmentLoginBinding
import com.carkzis.android.silenus.databinding.FragmentWelcomeBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var viewDataBinding: FragmentLoginBinding

    private lateinit var authorisation: FirebaseAuth

    val signIn = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(), this::onSignInResult)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = FragmentLoginBinding.inflate(inflater, container, false).apply {
            loginViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        authorisation = Firebase.auth

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLoginButton()
        setUpToast()

    }

    override fun onStart() {
        super.onStart()
        if (Firebase.auth.currentUser != null) {
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
            )
        }
    }

    private fun setUpLoginButton() {
        viewDataBinding.loginButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
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
        signIn.launch(intent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            viewModel.signedInToast(requireContext().getString(R.string.welcome) +
                    "${FirebaseAuth.getInstance().currentUser?.displayName}!")
            findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToWelcomeFragment()
            )

        } else {
            viewModel.signedInToast(requireContext().getString(R.string.sign_in_failed))
        }
    }

    private fun setUpToast() {
        viewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(message)
            }
        })
    }

}