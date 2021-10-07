package com.carkzis.android.silenus.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.mockito.Mockito

class ModelsTest {

    @Test
    fun yourReviewToDataObject_dataMapsCorrectly() {
        // Set up review to map to DataObject.
        val docId = "123"
        val name = "Testaurant"
        val rating = 5.0f
        val description = "This is for a test."
        val uid = "MRC123"
        val location = "Mantester"
        val time = Timestamp.now()
        val geoPoint = Mockito.mock(GeoPoint::class.java)
        val yourReview = YourReview(docId, name, rating,
            location, description, time,
            geoPoint)

        // Map YourReview object to ReviewDO
        val reviewDO = yourReview.toDataObject(uid)

        MatcherAssert.assertThat(reviewDO.id, CoreMatchers.`is`(docId))
        MatcherAssert.assertThat(reviewDO.establishment, CoreMatchers.`is`(name))
        MatcherAssert.assertThat(reviewDO.rating, CoreMatchers.`is`(rating))
        MatcherAssert.assertThat(reviewDO.location, CoreMatchers.`is`(location))
        MatcherAssert.assertThat(reviewDO.description, CoreMatchers.`is`(description))
        MatcherAssert.assertThat(reviewDO.dateAdded, CoreMatchers.`is`(time))
        MatcherAssert.assertThat(reviewDO.geo, CoreMatchers.`is`(geoPoint))
        MatcherAssert.assertThat(reviewDO.uid, CoreMatchers.`is`(uid))
    }

}