package com.carkzis.android.silenus.review

import android.location.Geocoder
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MapReason
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentEditReviewBinding
import com.carkzis.android.silenus.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class EditReviewFragment : Fragment() {

    private val viewModel by viewModels<EditReviewViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding: FragmentEditReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewDataBinding = FragmentEditReviewBinding.inflate(inflater, container, false)
            .apply {
            editReviewViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setHasOptionsMenu(true)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpFieldEntries()
        setUpToast()
        setUpLocationButton()
        setUpEditCompleteNavigation()
        handleOnBackPressed()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_rev_confirm_menu_button -> {
                Timber.e("Confirm alterations.")
                sharedViewModel.singleReview.value?.let { viewModel.submissionPreChecks(it) }
                true
            }
            R.id.edit_rev_quit_menu_button -> {
                Timber.e("Quit edit screen.")
                sharedViewModel.toastMe(getString(R.string.changes_aborted))
                findNavController().navigate(
                    EditReviewFragmentDirections.actionEditReviewFragmentToSingleReviewFragment()
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // TODO: Set up location button. Remember, we need to return here after going to the map.

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

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            sharedViewModel.resetReviewScreen()
            findNavController().navigate(
                EditReviewFragmentDirections.actionEditReviewFragmentToSingleReviewFragment()
            )
        }
    }

    /**
     * This listens to clicks on the location EditText to bring up the map fragment.
     */
    private fun setUpLocationButton() {
        viewDataBinding.editLocationBarEdittext.setOnClickListener {
            sharedViewModel.setBarDetails(
                viewModel.barName.value, viewModel.rating.value, viewModel.description.value)
            // Set the reason for opening the map.
            sharedViewModel.setMapOpenReason(MapReason.EDITREV)
            findNavController().navigate(
                EditReviewFragmentDirections.actionEditReviewFragmentToMapsFragment()
            )
        }
    }

    /**
     * Navigate back to the single review fragment after updating the shared viewmodel with the new
     * information.
     */
    private fun setUpEditCompleteNavigation() {
        viewModel.navToSingleReview.observe(viewLifecycleOwner, {
            sharedViewModel.resetReviewScreen()
            it.getContextIfNotHandled()?.let { editedReview ->
                sharedViewModel.setSingleReview(editedReview)
                findNavController().navigate(
                    EditReviewFragmentDirections.actionEditReviewFragmentToSingleReviewFragment()
                )
            }
        })
    }

    private fun setUpToast() {
        viewModel.toastText.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { message ->
                context?.showToast(requireContext().getString(message))
            }
        })
    }

}