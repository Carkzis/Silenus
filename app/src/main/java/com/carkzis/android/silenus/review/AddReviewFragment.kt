package com.carkzis.android.silenus.review

import android.location.Geocoder
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MapReason
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentAddReviewBinding
import com.carkzis.android.silenus.showToast
import com.firebase.ui.auth.AuthUI
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

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setUpSubmitButton()
        setUpLocationButton()
        setUpFieldEntries()
        handleOnBackPressed()
        setUpToast()

        setUpLogout()
        setUpNavigateToLogin()
        setUpNavigateToYourReviews() // To be changed to navigate to member's list of items.

    }

    override fun onStart() {
        super.onStart()

        sharedViewModel.authoriseUser()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_rev_menu_button -> {
                viewModel.submissionPreChecks()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
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

    /**
     * Set up the fields, so that when you return to the fragment from the MapFragment,
     * your fields are not emptied.
     */
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

    private fun setUpNavigateToYourReviews() {
        viewModel.navToYourReviews.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    sharedViewModel.resetReviewScreen()
                    findNavController().navigate(
                        AddReviewFragmentDirections.actionAddReviewFragmentToYourReviewsFragment()
                    )
            }
        })
    }


    /**
     * This will ensure, on pressing back, we don't go back to the Map but back to the
     * reviews fragment.
     */
    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sharedViewModel.resetReviewScreen()
            findNavController().navigate(
                AddReviewFragmentDirections.actionAddReviewFragmentToYourReviewsFragment()
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