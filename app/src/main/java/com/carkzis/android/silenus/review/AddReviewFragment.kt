package com.carkzis.android.silenus.review

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentAddReviewBinding
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddReviewFragment : Fragment() {

    private val viewModel by viewModels<AddReviewViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSubmitButton()
        setUpLocationButton()
        setUpLocationChosen()
        handleOnBackPressed()

    }

    override fun onStart() {
        super.onStart()

        if (authorisation.currentUser == null) {
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment()
            )
        } else if (Firebase.auth.currentUser?.displayName == null ||
            Firebase.auth.currentUser?.displayName == "") {
            logout(R.string.null_user_error)
        }
    }

    private fun setUpSubmitButton() {
        viewDataBinding.submitBarButton.setOnClickListener {
            viewModel.barSubmission()
        }
    }

    private fun setUpLocationButton() {
        viewDataBinding.locationBarEdittext.setOnClickListener {
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToMapsFragment()
            )
        }
    }

    private fun setUpLocationChosen() {
        sharedViewModel.chosenGeopoint.observe(viewLifecycleOwner, {
            it?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                viewModel.setUpLocationInfo(it, geocoder)
            }
        })
    }

    private fun logout(reason: Int) {
        AuthUI.getInstance().signOut(requireContext())
            .addOnCompleteListener {
                sharedViewModel.toastMe(getString(reason))
                findNavController().navigate(
                    AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment()
                )
            }
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sharedViewModel.resetGeopoint()
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToWelcomeFragment()
            )
        }
    }

}