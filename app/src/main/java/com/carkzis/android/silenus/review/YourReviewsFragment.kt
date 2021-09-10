package com.carkzis.android.silenus.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.MapReason
import com.carkzis.android.silenus.SharedViewModel
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.databinding.FragmentYourReviewsBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class YourReviewsFragment : Fragment(), SearchView.OnQueryTextListener {

    private val viewModel by viewModels<YourReviewsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding: FragmentYourReviewsBinding

    private lateinit var yourReviewsAdapter: YourReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = FragmentYourReviewsBinding.inflate(inflater, container, false).apply {
            yourReviewsViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        yourReviewsAdapter = yourReviewsAdapter()

        viewDataBinding.yourReviewsRecylerview.adapter = yourReviewsAdapter

        // Inflate the layout!
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDataObserver()
        setUpSearchView()
    }

    /**
     * Helper method to set up the review adapters methods initiated on their associated
     * click listeners with the adapter class.
     */
    private fun yourReviewsAdapter() : YourReviewsAdapter {
        return YourReviewsAdapter(YourReviewsAdapter.OnClickListener(
            {setUpMapClickListener(it)}, {setUpDescriptionClickListener()}))
    }

    /**
     * Helper method that is used as a higher order function with the YourReviewsAdapter,
     * which will direct the user to the location of the bar being reviewed on a map fragment.
     */
    private fun setUpMapClickListener(review: YourReview) {
        val geoPoint = review.geo
        sharedViewModel.setMapOpenReason(MapReason.VIEWREV)
        // TODO: We are passing in the geoPoints as strings.
        this.findNavController().navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToMapsFragment(
                arrayOf(geoPoint?.latitude.toString(), geoPoint?.longitude.toString()))
        )
    }

    /**
     * Helper method that is used as a higher order function within the YourReviewsAdapter,
     * which will perform an action on clicking the review items description box.
     */
    private fun setUpDescriptionClickListener() {
        this.findNavController().navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToMapsFragment(
                    arrayOf("20.007826849269325", "1.7928498610854149")))
    }

    /**
     * This observes the data in the recyclerview, and also resubmits a blank query
     * and returning to the fragment from the map.
     */
    private fun setUpDataObserver() {
        viewModel.yourReviews.observe(viewLifecycleOwner, {
            yourReviewsAdapter.addItemsToAdapter(it)
            // TODO: Can change this to use a viewModel that stores the query.
            onQueryTextSubmit("")
        })
    }

    /**
     * Sets up the the search view, which allows us to type a query and filter the
     * users reviews appropriately.
     */
    private fun setUpSearchView() {
        viewDataBinding.searchview.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?) : Boolean {
        yourReviewsAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?) : Boolean {
        yourReviewsAdapter.filter.filter(newText)
        return false
    }

}