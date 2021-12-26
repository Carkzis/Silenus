package com.carkzis.android.silenus.review

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.MapReason
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.data.YourReview
import com.carkzis.android.silenus.databinding.FragmentYourReviewsBinding
import com.carkzis.android.silenus.user.AuthCheck
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class YourReviewsFragment : Fragment(), SearchView.OnQueryTextListener, AuthCheck {

    private val viewModel by viewModels<YourReviewsViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var viewDataBinding: FragmentYourReviewsBinding

    private lateinit var yourReviewsAdapter: YourReviewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /*
         Set up data binding between the fragment and the layout. The lifecycleOwner observes
         the changes in LiveData in this databinding.
         */
        viewDataBinding = FragmentYourReviewsBinding.inflate(inflater, container, false).apply {
            yourReviewsViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        yourReviewsAdapter = yourReviewsAdapter()

        viewDataBinding.yourReviewsRecylerview.adapter = yourReviewsAdapter

        setHasOptionsMenu(true)

        // Inflate the layout!
        return viewDataBinding.root
    }

    /*
     * Used here to set up various observers/listeners.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDataObserver()
        setUpSearchView()
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
        inflater.inflate(R.menu.your_reviews_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_rev_menu_button -> {
                findNavController().navigate(
                    YourReviewsFragmentDirections.actionYourReviewsFragmentToAddReviewFragment()
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * This ensures the reviews are refreshed when either entering or returning to the fragment.
     */
    override fun onResume() {
        super.onResume()
        viewModel.refreshReviews()
    }

    /**
     * Helper method to set up the review adapters methods initiated on their associated
     * click listeners with the adapter class.
     */
    private fun yourReviewsAdapter() : YourReviewsAdapter {
        return YourReviewsAdapter(YourReviewsAdapter.OnClickListener(
            {setUpMapClickListener(it)}, {setUpDescriptionClickListener(it)}))
    }

    /**
     * Helper method that is used as a higher order function with the YourReviewsAdapter,
     * which will direct the user to the location of the bar being reviewed on a map fragment.
     */
    private fun setUpMapClickListener(review: YourReview) {
        val geoPoint = review.geo
        sharedViewModel.setMapOpenReason(MapReason.VIEWREV)
        Timber.e(geoPoint?.latitude.toString())
        Timber.e(geoPoint?.longitude.toString())
        this.findNavController().navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToMapsFragment(
                arrayOf(geoPoint?.latitude.toString(), geoPoint?.longitude.toString()))
        )
    }

    /**
     * Helper method that is used as a higher order function within the YourReviewsAdapter,
     * which will perform an action on clicking the review items description box.
     */
    private fun setUpDescriptionClickListener(review: YourReview) {
        sharedViewModel.setSingleReview(review)
        this.findNavController().navigate(
            YourReviewsFragmentDirections.actionYourReviewsFragmentToSingleReviewFragment())
    }

    /**
     * This observes the data in the recyclerview, and also resubmits a blank query
     * and returning to the fragment from the map.
     */
    private fun setUpDataObserver() {
        viewModel.yourReviews.observe(viewLifecycleOwner, {
            yourReviewsAdapter.addItemsToAdapter(it)
            // This resubmits a blank query.
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

    override fun setUpLogout() {
        sharedViewModel.logout.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let { reason ->
                AuthUI.getInstance().signOut(requireContext())
                    .addOnCompleteListener {
                        sharedViewModel.toastMe(getString(reason))
                        findNavController().navigate(
                            YourReviewsFragmentDirections.actionYourReviewsFragmentToLoginFragment()
                        )
                    }
            }
        })
    }

    override fun setUpNavigateToLogin() {
        sharedViewModel.navToLogin.observe(viewLifecycleOwner, {
            it.getContextIfNotHandled()?.let {
                findNavController().navigate(
                    YourReviewsFragmentDirections.actionYourReviewsFragmentToLoginFragment()
                )
            }
        })
    }

}