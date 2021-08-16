package com.carkzis.android.silenus.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentWelcomeBinding
import com.carkzis.android.silenus.showToast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding : FragmentWelcomeBinding

    private lateinit var authorisation: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentWelcomeBinding.inflate(inflater, container, false).apply {
            welcomeViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        authorisation = Firebase.auth

        return viewDataBinding.root

    }

    override fun onStart() {
        super.onStart()

        sharedViewModel.authoriseUser()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLogoutFab()
        setUpReviewsFab()
        setUpLogout()
        setUpNavigateToLogin()
        setUpUserDetails()

    }

    private fun setUpLogoutFab() {
        viewDataBinding.logoutFab.setOnClickListener {
            sharedViewModel.chooseLogout()
        }
    }


    private fun setUpReviewsFab() {
        viewDataBinding.reviewsFab.setOnClickListener {
            findNavController().navigate(
                // TODO: This is subject to change to the fragment with the list of reviews.
                WelcomeFragmentDirections.actionWelcomeFragmentToAddReviewFragment()
            )
        }
    }

    private fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            findNavController().navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
            )
        })
    }

    private fun setUpUserDetails() {
        sharedViewModel.username.observe(viewLifecycleOwner, {
            viewModel.setUsername()
        })
    }

    private fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        sharedViewModel.toastMe(getString(reason))
                        findNavController().navigate(
                            WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
                        )
                    }
            }
        })
    }

}