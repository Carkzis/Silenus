package com.carkzis.android.silenus.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentWelcomeBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    private val viewModel by viewModels<WelcomeViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    @Inject
    lateinit var firebaseAuth : FirebaseAuth

    private lateinit var viewDataBinding : FragmentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
         Set up data binding between the fragment and the layout. The lifecycleOwner observes
         the changes in LiveData in this databinding.
         */
        viewDataBinding = FragmentWelcomeBinding.inflate(inflater, container, false).apply {
            welcomeViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        return viewDataBinding.root

    }

    override fun onStart() {
        super.onStart()

        /*
         We request an authorisation of the user; if this fails, the user is directed
         to the LoginFragment.
         */
        sharedViewModel.authoriseUser()

    }

    /*
     * Used here to set up various observers/listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLogoutFab()
        setUpReviewsFab()
        setUpLogout()
        setUpNavigateToLogin()
        setUpUserDetails()

    }

    /**
     * Sets up the fab for logging out the user.
     */
    private fun setUpLogoutFab() {
        viewDataBinding.logoutFab.setOnClickListener {
            sharedViewModel.chooseLogout()
        }
    }

    /**
     * Sets up the fab for directing the user to the YourReviewsFragment.
     */
    private fun setUpReviewsFab() {
        viewDataBinding.reviewsFab.setOnClickListener {
            findNavController().navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToYourReviewsFragment()
            )
        }
    }

    /**
     * Sets up the navigation to the LoginFragment.
     */
    private fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, { it ->
            it.getContextIfNotHandled()?.let { reason ->
                if (reason) {
                    findNavController().navigate(
                        WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
                    )
                }
            }
        })
    }

    /**
     * Applies the current username observed in the SharedViewModel to the username
     * LiveData in the WelcomeViewModel.
     */
    private fun setUpUserDetails() {
        sharedViewModel.username.observe(viewLifecycleOwner, {
            viewModel.setUsername()
        })
    }

    /**
     * This observes when the user should be logged out, and directed to the LoginFragment.
     */
    private fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                   firebaseAuth.signOut()
                    sharedViewModel.toastMe(getString(reason))
                    findNavController().navigate(
                        WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment()
                    )
                }
            })
        }

}