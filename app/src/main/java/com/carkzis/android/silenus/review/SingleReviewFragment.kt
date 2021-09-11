package com.carkzis.android.silenus.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.carkzis.android.silenus.R
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

    private fun setUpReviewInformation() {
        sharedViewModel.singleReview.observe(viewLifecycleOwner, {
            it.let {
                Timber.e("Setting up review information.")
                viewModel.setUpRev(it)
            }
        })
    }

}