package com.carkzis.android.silenus.review

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.carkzis.android.silenus.R
import com.carkzis.android.silenus.data.SharedViewModel
import com.carkzis.android.silenus.databinding.FragmentEditReviewBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

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
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}