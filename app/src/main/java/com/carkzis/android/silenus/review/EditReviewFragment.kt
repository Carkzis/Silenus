package com.carkzis.android.silenus.review

import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentEditReviewBinding
import com.carkzis.android.silenus.showToast
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
        setUpEditCompleteNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_rev_confirm_menu_button -> {
                Timber.e("Confirm alterations.")
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
        sharedViewModel.singleReview.observe(viewLifecycleOwner, {
            it?.let {
                val geocoder = Geocoder(context, Locale.getDefault())
                viewModel.setUpReviewInfo(it, geocoder)
            }
        })
    }

    /**
     * Navigate back to the single review fragment after updating the shared viewmodel with the new
     * information.
     */
    private fun setUpEditCompleteNavigation() {
        viewModel.navToSingleReview.observe(viewLifecycleOwner, {
            // TODO: SharedViewModel needs updating.
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