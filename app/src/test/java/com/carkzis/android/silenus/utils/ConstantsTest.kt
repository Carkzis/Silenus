package com.carkzis.android.silenus.utils

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class ConstantsTest {

    @Test
    fun getCollectionName_enumValuesReturnedAsStrings() {
        // Given an enum values of of Constants enum class.
        val reviewsConstant = Constants.REVIEWS
        val usersConstant = Constants.USERS

        // Call the method to return the string representations.
        val revStrRepresentation = getCollectionName(reviewsConstant)
        val userStrRepresentation = getCollectionName(usersConstant)

        // Assert that we get the expected constants.
        assertThat(revStrRepresentation, `is`("reviews"))
        assertThat(userStrRepresentation, `is`("users"))
    }

}