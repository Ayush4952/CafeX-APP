package com.example.cafex.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Person
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
fun RegisterScreen(
    uiState: AuthUiState,
    onRegister: (String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        AuthScaffold(
            title = "Join CafeX",
            subtitle = "Create your account and share the perfect menu",
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full name") },
                leadingIcon = {
                    Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(12.dp))

            PasswordField(
                value = password,
                onValueChange = { password = it },
                passwordVisible = passwordVisible,
                onVisibilityClick = { passwordVisible = !passwordVisible },
            )

            Spacer(modifier = Modifier.height(12.dp))

            PasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                passwordVisible = confirmPasswordVisible,
                onVisibilityClick = {
                    confirmPasswordVisible = !confirmPasswordVisible
                },
                label = "Confirm password",
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onRegister(fullName, email, password, confirmPassword) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text("Create account")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Already have an account? Sign in")
            }
        }

        LoadingOverlay(visible = uiState.isLoading)
    }
}
