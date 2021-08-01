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
    private val sharedViewModel by activityViewModels<UserViewModel>()

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

        if (authorisation.currentUser == null) {
            findNavController().navigate(
                WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }

        return viewDataBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLogoutFab()

    }

    private fun setUpLogoutFab() {
        viewDataBinding.logoutFab.setOnClickListener {
            AuthUI.getInstance().signOut(requireContext())
                .addOnCompleteListener {
                    viewModel.signedOutToast(getString(R.string.logged_out))
                    findNavController().navigate(
                        WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
                }
        }
    }
}