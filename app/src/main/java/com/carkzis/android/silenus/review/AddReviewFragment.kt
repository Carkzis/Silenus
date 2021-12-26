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
import com.carkzis.android.silenus.user.AuthCheck
import com.carkzis.android.silenus.utils.showToast
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AddReviewFragment : Fragment(), AuthCheck {

    private val viewModel by viewModels<AddReviewViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding : FragmentAddReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
         Set up data binding between the fragment and the layout. The lifecycleOwner observes
         the changes in LiveData in this databinding.
         */
        viewDataBinding = FragmentAddReviewBinding.inflate(inflater, container, false).apply {
            addReviewViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return viewDataBinding.root
    }

    /*
     * Used here to set up various observers/listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        /*
         We request an authorisation of the user; if this fails, the user is directed
         to the LoginFragment.
         */
        sharedViewModel.authoriseUser()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_rev_frag_menu_button -> {
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

    /**
     * This observes when the user should be logged out, and directed to the LoginFragment.
     */
    override fun setUpLogout() {
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

    /**
     * Navigates the user to the login if directed to do so.
     */
    override fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    findNavController().navigate(
                        AddReviewFragmentDirections.actionAddReviewFragmentToLoginFragment()
                    )
            }
        })
    }

    /**
     * Sets up the navigation to the list of the users reviews.
     */
    private fun setUpNavigateToYourReviews() {
        viewModel.navToYourReviews.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                    /*
                    We want to reset the add/edit review screens, as we are submitted the
                    additions/changes.
                     */
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

    /**
     * Sets up the ability to show a toast once by observing the LiveData in the ViewModel.
     */
    private fun setUpToast() {
        viewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(requireContext().getString(message))
            }
        })
    }

}