package com.example.cafex.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.cafex.di.AppViewModelFactory
import com.example.cafex.navigation.CafeXNavGraph
import com.example.cafex.viewmodel.AuthViewModel
import com.example.cafex.viewmodel.CafeViewModel

@Composable
fun CafeXApp(
    viewModelFactory: AppViewModelFactory,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val cafeViewModel: CafeViewModel = viewModel(factory = viewModelFactory)
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val cafeUiState by cafeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val authMessage = authUiState.errorMessage ?: authUiState.infoMessage
    LaunchedEffect(authMessage) {
        if (authMessage != null) {
            snackbarHostState.showSnackbar(authMessage)
            authViewModel.clearMessage()
        }
    }

    val cafeMessage = cafeUiState.errorMessage ?: cafeUiState.infoMessage
    LaunchedEffect(cafeMessage) {
        if (cafeMessage != null) {
            snackbarHostState.showSnackbar(cafeMessage)
            cafeViewModel.clearMessage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        CafeXNavGraph(
            navController = navController,
            authUiState = authUiState,
            cafeUiState = cafeUiState,
            authViewModel = authViewModel,
            cafeViewModel = cafeViewModel,
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
        )
    }
}
