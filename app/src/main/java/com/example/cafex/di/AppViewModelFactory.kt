package com.example.cafex.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cafex.viewmodel.AuthViewModel
import com.example.cafex.viewmodel.CafeViewModel

class AppViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
            AuthViewModel(
                repository = container.authRepository,
                isFirebaseEnabled = container.isFirebaseEnabled,
                isDatabaseEnabled = container.isDatabaseEnabled,
            ) as T
        }

        modelClass.isAssignableFrom(CafeViewModel::class.java) -> {
            CafeViewModel(repository = container.cafeRepository) as T
        }

        else -> error("Unknown ViewModel class: ${modelClass.name}")
    }
}
