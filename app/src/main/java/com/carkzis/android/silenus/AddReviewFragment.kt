package com.carkzis.android.silenus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.databinding.FragmentAddReviewBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReviewFragment : Fragment() {

    private val viewModel by viewModels<AddReviewViewModel>()
    private val sharedViewModel by activityViewModels<UserViewModel>()

    private lateinit var viewDataBinding : FragmentAddReviewBinding

    private lateinit var authorisation: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentAddReviewBinding.inflate(inflater, container, false).apply {
            addReviewViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        authorisation = Firebase.auth

        // Inflate the layout for this fragment
        return viewDataBinding.root
    }

    override fun onStart() {
        super.onStart()

        if (authorisation.currentUser == null) {
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment())
        } else if (Firebase.auth.currentUser?.displayName == null ||
            Firebase.auth.currentUser?.displayName == "") {
            logout(R.string.null_user_error)
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