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
import com.carkzis.android.silenus.MapReason
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentAddReviewBinding
import com.carkzis.android.silenus.showToast
import com.carkzis.android.silenus.welcome.WelcomeFragmentDirections
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentAddReviewBinding.inflate(inflater, container, false).apply {
            addReviewViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        // Inflate the layout for this fragment
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSubmitButton()
        setUpLocationButton()
        setUpFieldEntries()
        handleOnBackPressed()
        setUpToast()

        setUpLogout()
        setUpNavigateToLogin()
        setUpNavigateToWelcome() // To be changed to navigate to member's list of items.

    }

    override fun onStart() {
        super.onStart()

        sharedViewModel.authoriseUser()

    }

    private fun setUpSubmitButton() {
        viewDataBinding.submitBarButton.setOnClickListener {
            viewModel.submissionPreChecks()
        }
    }

    /**
     * This listens to clicks on the location EditText to bring up the map fragment.
     */
    private fun setUpLocationButton() {
        viewDataBinding.locationBarEdittext.setOnClickListener {
            sharedViewModel.setBarDetails(
                viewModel.barName.value, viewModel.rating.value, viewModel.description.value)
            // Set the reason for opening the map.
            sharedViewModel.setMapOpenReason(MapReason.ADDREV)
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToMapsFragment()
            )
        }
    }

    private fun setUpFieldEntries() {
        sharedViewModel.chosenGeopoint.observe(viewLifecycleOwner, {
            it?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                viewModel.setUpLocationInfo(it, geocoder)
            }
        })
        sharedViewModel.reviewBarName.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setUpBarName(it)
            }
        })
        sharedViewModel.reviewRating.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setUpRating(it)
            }
        })
        sharedViewModel.reviewDescription.observe(viewLifecycleOwner, {
            it?.let {
                viewModel.setUpDescription(it)
            }
        })
    }

    private fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        sharedViewModel.toastMe(getString(reason))
                        findNavController().navigate(
                            AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment()
                        )
                    }
            }
        })
    }

    private fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    findNavController().navigate(
                        AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment()
                    )
            }
        })
    }

    // TODO: This will actually be changed to nagivate to the member's list of reviews.
    private fun setUpNavigateToWelcome() {
        viewModel.navToWelcome.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    sharedViewModel.resetReviewScreen()
                    findNavController().navigate(
                        AddReviewFragmentDirections.actionAddReviewFragmentToWelcomeFragment()
                    )
            }
        })
    }


    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sharedViewModel.resetReviewScreen()
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToWelcomeFragment()
            )
        }
    }

    private fun setUpToast() {
        viewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(requireContext().getString(message))
            }
        })
    }

}