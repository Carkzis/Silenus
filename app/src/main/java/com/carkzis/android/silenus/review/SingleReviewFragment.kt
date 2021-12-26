package com.carkzis.android.silenus.review

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MapReason
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentSingleReviewBinding
import com.carkzis.android.silenus.user.AuthCheck
import com.carkzis.android.silenus.utils.showToast
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SingleReviewFragment : Fragment(), AuthCheck {

    private val viewModel by viewModels<SingleReviewViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding : FragmentSingleReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
         Set up data binding between the fragment and the layout. The lifecycleOwner observes
         the changes in LiveData in this databinding.
         */
        viewDataBinding = FragmentSingleReviewBinding.inflate(inflater, container, false).apply {
            singleReviewViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
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

        setUpReviewInformation()
        setUpToast()
        setUpDeletionCompleteNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.single_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.view_location_menu_button -> {
                // Navigate to the MapFragment using the GeoPoint for the current review.
                sharedViewModel.setMapOpenReason(MapReason.VIEWREV)
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToMapsFragment(
                        viewModel.getGeo()
                    )
                )
                true
            }
            R.id.edit_rev_menu_button -> {
                /*
                This will take me to the edit review fragment.
                 We are setting bar details from the model, as the edit fragment needs to go
                 to a from the Mars Fragment.
                 */
                sharedViewModel.setBarDetailsFromModel()
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToEditReviewFragment()
                )
                true
            }
            R.id.delete_rev_button -> {
                // This will attempt the deletion of a Review.
                openDeleteDialogue()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Opens a dialogue to ask the user to confirm that they do indeed want to delete the
     * current opened review.
     */
    private fun openDeleteDialogue() {
        val builder = AlertDialog.Builder(view?.context)
        builder.setTitle("Review Deletion")
            .setMessage("Are you sure you want to delete this review?")
            .setPositiveButton(R.string.yes) { _, _ ->
                sharedViewModel.singleReview.value?.let { viewModel.progressToDeletingReview(it) }
            }
            .setNegativeButton(R.string.no) { _, _ ->
                // Do nothing.
            }
        builder.show()
    }

    /**
     * Navigate back to the single review fragment after deleting the Review with the new
     * information.
     */
    private fun setUpDeletionCompleteNavigation() {
        viewModel.navToYourReviews.observe(viewLifecycleOwner, {
            sharedViewModel.resetReviewScreen()
            it.getContextIfNotHandled()?.let {
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToYourReviewsFragment()
                )
            }
        })
    }

    /**
     * This retrieves the current review information from the shared viewmodel and sends it to
     * the current viewmodel.
     */
    private fun setUpReviewInformation() {
        sharedViewModel.singleReview.observe(viewLifecycleOwner, {
            it.let {
                Timber.e("Setting up review information.")
                viewModel.setUpRev(it)
            }
        })
    }

    /**
     * Sets up the ability to show a toast once by observing the LiveData in the ViewModel.
     */
    private fun setUpToast() {
        sharedViewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(message)
            }
        })
    }

    override fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        sharedViewModel.toastMe(getString(reason))
                        findNavController().navigate(
                            SingleReviewFragmentDirections.actionSingleReviewFragmentToLoginFragment()
                        )
                    }
            }
        })
    }

    override fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToLoginFragment()
                )
            }
        })
    }

}