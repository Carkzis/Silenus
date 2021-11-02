package com.carkzis.android.silenus.review

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.*

class AddReviewFragmentTest {

    // TODO: Note the following useful code, it simulates a long press.
    fun usefulCode() {
        val device : UiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.swipe(10,10,10,10,400)
    }

}