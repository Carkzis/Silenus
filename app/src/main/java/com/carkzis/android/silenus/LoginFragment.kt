package com.carkzis.android.silenus

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.databinding.FragmentLoginBinding
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()
    private val sharedViewModel by activityViewModels<UserViewModel>()

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
        setUpLoginIntentListener()

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
            viewModel.signIn()
        }
    }

    private fun setUpLoginIntentListener() {
        viewModel.loginIntent.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { intent ->
                signIn.launch(intent)
            }
        })
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            result.idpResponse
            viewModel.signedInToast(requireContext().getString(R.string.welcome) +
                    "${FirebaseAuth.getInstance().currentUser?.displayName}!")
            // Need to create a user document in firestore.
            sharedViewModel.addUser()
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