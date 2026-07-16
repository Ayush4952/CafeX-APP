package com.example.cafex.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cafex.ui.components.AuthScaffold
import com.example.cafex.ui.components.LoadingOverlay
import com.example.cafex.ui.components.PasswordField
import com.example.cafex.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPassword: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AuthScaffold(
            title = "Welcome back",
            subtitle = if (uiState.isFirebaseEnabled) {
                "Sign in to order your next favorite"
            } else {
                "Demo mode • use any valid email and 6+ character password"
            },
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email address") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(14.dp))

            PasswordField(
                value = password,
                onValueChange = { password = it },
                passwordVisible = passwordVisible,
                onVisibilityClick = { passwordVisible = !passwordVisible },
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { onForgotPassword(email) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Forgot password?")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onLogin(email, password) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text("Sign in")
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("New to CafeX? Create an account")
            }
        }

        LoadingOverlay(visible = uiState.isLoading)
    }
}
