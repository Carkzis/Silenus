package com.carkzis.android.silenus.review

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
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SingleReviewFragment : Fragment() {

    private val viewModel by viewModels<SingleReviewViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding : FragmentSingleReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        sharedViewModel.authoriseUser()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpReviewInformation()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.single_review_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.view_location_menu_button -> {
                sharedViewModel.setMapOpenReason(MapReason.VIEWREV)
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToMapsFragment(
                        viewModel.getGeo()
                    )
                )
                true
            }
            R.id.edit_rev_menu_button -> {
                Timber.e("This will take me to the edit review fragment.")
                findNavController().navigate(
                    SingleReviewFragmentDirections.actionSingleReviewFragmentToEditReviewFragment()
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setUpReviewInformation() {
        sharedViewModel.singleReview.observe(viewLifecycleOwner, {
            it.let {
                Timber.e("Setting up review information.")
                viewModel.setUpRev(it)
            }
        })
    }

}