package com.example.cafex.utils

private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

object Validation {
    fun emailError(email: String): String? = when {
        email.isBlank() -> "Please enter your email."
        !emailRegex.matches(email.trim()) -> "Please enter a valid email address."
        else -> null
    }

    fun passwordError(password: String): String? = when {
        password.isBlank() -> "Please enter your password."
        password.length < 6 -> "Password must contain at least 6 characters."
        else -> null
    }

    fun registrationError(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): String? = when {
        fullName.isBlank() -> "Please enter your full name."
        emailError(email) != null -> emailError(email)
        passwordError(password) != null -> passwordError(password)
        confirmPassword.isBlank() -> "Please confirm your password."
        password != confirmPassword -> "Passwords do not match."
        else -> null
    }

    fun itemError(name: String, price: String, description: String): String? = when {
        name.isBlank() -> "Please enter an item name."
        price.toDoubleOrNull() == null || price.toDouble() <= 0.0 -> "Enter a valid price."
        description.isBlank() -> "Please enter a description."
        else -> null
    }
}
