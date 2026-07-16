package com.example.cafex.repository

import com.example.cafex.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    database: DatabaseReference?,
) : AuthRepository {
    private val usersReference = database?.child("users")

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            trySend(
                firebaseUser?.let {
                    User(
                        id = it.uid,
                        fullName = it.displayName.orEmpty().ifBlank { "CafeX User" },
                        email = it.email.orEmpty(),
                    )
                },
            )
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = requireNotNull(result.user) { "Firebase did not return a user." }
        User(
            id = firebaseUser.uid,
            fullName = firebaseUser.displayName.orEmpty().ifBlank { "CafeX User" },
            email = firebaseUser.email.orEmpty(),
        )
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
    ): Result<User> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val firebaseUser = requireNotNull(result.user) { "Firebase did not return a user." }

        firebaseUser.updateProfile(
            userProfileChangeRequest {
                displayName = fullName.trim()
            },
        ).await()

        val user = User(
            id = firebaseUser.uid,
            fullName = fullName.trim(),
            email = email.trim(),
            createdAt = System.currentTimeMillis(),
        )

        usersReference?.child(user.id)?.setValue(user)?.await()
        user
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email.trim()).await()
        Unit
    }

    override fun logout() {
        auth.signOut()
    }
}
