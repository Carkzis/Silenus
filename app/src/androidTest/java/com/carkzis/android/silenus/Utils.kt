package com.carkzis.android.silenus

import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

fun clickRecyclerViewChildView(id: Int) = object: ViewAction {
    override fun getConstraints(): Matcher<View>? = null

    override fun getDescription(): String = "Click a child view in the RecyclerView."

    override fun perform(uiController: UiController?, view: View?) {
        val childView = view?.findViewById<View>(id)
        childView?.performClick()
    }
}