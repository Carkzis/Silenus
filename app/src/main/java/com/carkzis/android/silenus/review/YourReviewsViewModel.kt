package com.carkzis.android.silenus.review

import androidx.lifecycle.ViewModel
import com.carkzis.android.silenus.data.MainRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class YourReviewsViewModel @Inject constructor(
    val repository: MainRepository,
    val authorisation: FirebaseAuth
) : ViewModel() {



}