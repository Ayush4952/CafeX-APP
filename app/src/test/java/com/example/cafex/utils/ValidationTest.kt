package com.example.cafex.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidationTest {
    @Test
    fun validLoginFieldsReturnNoErrors() {
        assertNull(Validation.emailError("student@example.com"))
        assertNull(Validation.passwordError("coffee123"))
    }

    @Test
    fun malformedEmailReturnsHelpfulError() {
        assertEquals(
            "Please enter a valid email address.",
            Validation.emailError("not-an-email"),
        )
    }

    @Test
    fun mismatchedRegistrationPasswordsReturnError() {
        assertEquals(
            "Passwords do not match.",
            Validation.registrationError(
                fullName = "Cafe Student",
                email = "student@example.com",
                password = "coffee123",
                confirmPassword = "coffee456",
            ),
        )
    }
}
