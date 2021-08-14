package com.carkzis.android.silenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.databinding.FragmentWelcomeBinding
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

        if (authorisation.currentUser == null) {
            findNavController().navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        } else if (Firebase.auth.currentUser?.displayName == null ||
            Firebase.auth.currentUser?.displayName == "") {
            logout(R.string.null_user_error)
        } else {
            viewModel.setUsername()
            sharedViewModel.addUser()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLogoutFab()
        setUpReviewsFab()

    }

    private fun setUpLogoutFab() {
        viewDataBinding.logoutFab.setOnClickListener {
            logout(R.string.logged_out)
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

    private fun logout(reason: Int) {
        AuthUI.getInstance().signOut(requireContext())
            .addOnCompleteListener {
                sharedViewModel.toastMe(getString(reason))
                findNavController().navigate(
                    WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
            }
    }


}