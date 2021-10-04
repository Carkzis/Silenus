package com.carkzis.android.silenus.di

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * This allows us to use launchFragmentInContainer during testing
 * (but as launchFragmentInHiltContainer).
 */
@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity()