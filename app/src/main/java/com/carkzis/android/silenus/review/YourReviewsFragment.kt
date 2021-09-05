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
import com.carkzis.android.silenus.SharedViewModel
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

        // This has been placed here for now.
        yourReviewsAdapter = YourReviewsAdapter(YourReviewsAdapter.OnClickListener {
            this.findNavController().navigate(
                YourReviewsFragmentDirections.actionYourReviewsFragmentToMapsFragment())
        })

        viewDataBinding.yourReviewsRecylerview.adapter = yourReviewsAdapter

        // Inflate the layout!
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDataObserver()
        setUpSearchView()
        setUpItemListener()
    }

    private fun setUpItemListener() {

    }

    private fun setUpDataObserver() {
        viewModel.yourReviews.observe(viewLifecycleOwner, {
            yourReviewsAdapter.addItemsToAdapter(it)
        })
    }

    private fun setUpSearchView() {
        viewDataBinding.searchview.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?) : Boolean {
        Timber.e("are you doing something")
        yourReviewsAdapter.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(newText: String?) : Boolean {
        Timber.e("are you doing something")
        yourReviewsAdapter.filter.filter(newText)
        return false
    }

}