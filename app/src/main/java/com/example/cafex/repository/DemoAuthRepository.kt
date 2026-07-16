package com.example.cafex.repository

import com.example.cafex.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DemoAuthRepository : AuthRepository {
    private val userState = MutableStateFlow<User?>(null)

    override val currentUser: Flow<User?> = userState

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        delay(450)
        User(
            id = "demo-user",
            fullName = "CafeX Guest",
            email = email.trim(),
            createdAt = System.currentTimeMillis(),
        ).also { userState.value = it }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
    ): Result<User> = runCatching {
        delay(550)
        User(
            id = "demo-user",
            fullName = fullName.trim(),
            email = email.trim(),
            createdAt = System.currentTimeMillis(),
        ).also { userState.value = it }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        delay(350)
    }

    override fun logout() {
        userState.value = null
    }
}
