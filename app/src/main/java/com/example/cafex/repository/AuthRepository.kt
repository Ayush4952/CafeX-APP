package com.example.cafex.repository

import com.example.cafex.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(fullName: String, email: String, password: String): Result<User>

    suspend fun sendPasswordReset(email: String): Result<Unit>

    fun logout()
}
