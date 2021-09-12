package com.carkzis.android.silenus.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentEditReviewBinding
import dagger.hilt.android.AndroidEntryPoint

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

        return viewDataBinding.root
    }

    // TODO: X will send user back after confirming decision. Tick will also ask to confirm.

}