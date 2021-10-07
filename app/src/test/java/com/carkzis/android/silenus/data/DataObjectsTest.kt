package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito

class DataObjectsTest {

    @Test
    fun reviewDOToUIModel_dataMapsCorrectly() {
        // Set up review to map to DataObject.
        val docId = "123"
        val name = "Testaurant"
        val rating = 5.0f
        val description = "This is for a test."
        val uid = "MRC123"
        val location = "Mantester"
        val time = Timestamp.now()
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        val reviewDO = ReviewDO(docId, name, rating,
            location, geoPoint, description, time,
            uid)

        // Map YourReview object to ReviewDO
        val yourReview = reviewDO.toUIModel()

        assertThat(yourReview.documentId, `is`(docId))
        assertThat(yourReview.establishment, `is`(name))
        assertThat(yourReview.rating, `is`(rating))
        assertThat(yourReview.location, `is`(location))
        assertThat(yourReview.description, `is`(description))
        assertThat(yourReview.dateAdded, `is`(time))
        assertThat(yourReview.geo, `is`(geoPoint))
    }


}