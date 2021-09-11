package com.carkzis.android.silenus.review

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.carkzis.android.silenus.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingleReviewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_review, container, false)
    }

}