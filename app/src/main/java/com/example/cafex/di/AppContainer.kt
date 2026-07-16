package com.example.cafex.di

import android.content.Context
import com.example.cafex.BuildConfig
import com.example.cafex.repository.AuthRepository
import com.example.cafex.repository.CafeRepository
import com.example.cafex.repository.DemoAuthRepository
import com.example.cafex.repository.DemoCafeRepository
import com.example.cafex.repository.FirebaseAuthRepository
import com.example.cafex.repository.FirebaseCafeRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AppContainer(context: Context) {
    val isFirebaseEnabled: Boolean
    val authRepository: AuthRepository
    val cafeRepository: CafeRepository

    init {
        val firebaseApp = if (BuildConfig.FIREBASE_ENABLED) {
            FirebaseApp.getApps(context).firstOrNull() ?: FirebaseApp.initializeApp(context)
        } else {
            null
        }

        isFirebaseEnabled = firebaseApp != null

        if (firebaseApp != null) {
            val database = FirebaseDatabase.getInstance(firebaseApp).reference
            authRepository = FirebaseAuthRepository(
                auth = FirebaseAuth.getInstance(firebaseApp),
                database = database,
            )
            cafeRepository = FirebaseCafeRepository(database)
        } else {
            authRepository = DemoAuthRepository()
            cafeRepository = DemoCafeRepository()
        }
    }
}
