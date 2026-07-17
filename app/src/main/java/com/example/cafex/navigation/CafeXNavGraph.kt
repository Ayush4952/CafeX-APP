package com.example.cafex.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cafex.ui.screens.AddEditItemScreen
import com.example.cafex.ui.screens.CartScreen
import com.example.cafex.ui.screens.DashboardScreen
import com.example.cafex.ui.screens.DetailScreen
import com.example.cafex.ui.screens.HomeScreen
import com.example.cafex.ui.screens.LoginScreen
import com.example.cafex.ui.screens.ProfileScreen
import com.example.cafex.ui.screens.RegisterScreen
import com.example.cafex.ui.screens.SplashScreen
import com.example.cafex.viewmodel.AuthUiState
import com.example.cafex.viewmodel.AuthViewModel
import com.example.cafex.viewmodel.CafeUiState
import com.example.cafex.viewmodel.CafeViewModel

@Composable
fun CafeXNavGraph(
    navController: NavHostController,
    authUiState: AuthUiState,
    cafeUiState: CafeUiState,
    authViewModel: AuthViewModel,
    cafeViewModel: CafeViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        modifier = modifier,
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                sessionReady = !authUiState.isCheckingSession,
                onFinished = {
                    val destination = if (authUiState.user == null) Routes.LOGIN else Routes.DASHBOARD
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.LOGIN) {
            LaunchedEffect(authUiState.user?.id) {
                if (authUiState.user != null) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            LoginScreen(
                uiState = authUiState,
                onLogin = authViewModel::login,
                onSignUpClick = { navController.navigate(Routes.REGISTER) },
                onForgotPassword = authViewModel::sendPasswordReset,
            )
        }

        composable(Routes.REGISTER) {
            LaunchedEffect(authUiState.user?.id) {
                if (authUiState.user != null) {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }

            RegisterScreen(
                uiState = authUiState,
                onRegister = authViewModel::register,
                onLoginClick = { navController.popBackStack() },
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                user = authUiState.user,
                uiState = cafeUiState,
                onOpenMenu = { navController.navigate(Routes.HOME) },
                onAddItem = { navController.navigate(Routes.ADD_ITEM) },
                onCartClick = { navController.navigate(Routes.CART) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onItemClick = { navController.navigate(Routes.detail(it.id)) },
                onMessageShown = cafeViewModel::clearMessage,
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                user = authUiState.user,
                uiState = cafeUiState,
                databaseEnabled = authUiState.isDatabaseEnabled,
                onSearchChange = cafeViewModel::setSearchQuery,
                onCategorySelected = cafeViewModel::selectCategory,
                onSortOrderSelected = cafeViewModel::setSortOrder,
                onAvailableOnlyToggle = cafeViewModel::toggleAvailableOnly,
                onFavoritesOnlyToggle = cafeViewModel::toggleFavoritesOnly,
                onFavoriteToggle = cafeViewModel::toggleFavorite,
                onAddToCart = cafeViewModel::addToCart,
                onItemClick = { navController.navigate(Routes.detail(it.id)) },
                onAddClick = { navController.navigate(Routes.ADD_ITEM) },
                onDashboardClick = {
                    if (!navController.popBackStack(Routes.DASHBOARD, inclusive = false)) {
                        navController.navigate(Routes.DASHBOARD)
                    }
                },
                onCartClick = { navController.navigate(Routes.CART) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onMessageShown = cafeViewModel::clearMessage,
            )
        }

        composable(Routes.CART) {
            CartScreen(
                lines = cafeUiState.cartLines,
                total = cafeUiState.cartTotal,
                onBack = { navController.popBackStack() },
                onIncrease = cafeViewModel::increaseCartQuantity,
                onDecrease = cafeViewModel::decreaseCartQuantity,
                onClear = cafeViewModel::clearCart,
                onCheckout = {
                    cafeViewModel.placeOrder {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(Routes.ADD_ITEM) {
            AddEditItemScreen(
                existingItem = null,
                isSaving = cafeUiState.isSaving,
                initialCategoryId = cafeUiState.selectedCategoryId
                    .takeUnless { it == "all" }
                    ?: "coffee",
                onBack = { navController.popBackStack() },
                onSave = { name, price, description, categoryId, available ->
                    cafeViewModel.addItem(
                        name = name,
                        price = price,
                        description = description,
                        categoryId = categoryId,
                        available = available,
                        userId = authUiState.user?.id.orEmpty(),
                        onSuccess = { navController.popBackStack() },
                    )
                },
            )
        }

        composable(
            route = Routes.DETAIL_PATTERN,
            arguments = listOf(navArgument(Routes.ITEM_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(Routes.ITEM_ID).orEmpty()
            val item = cafeViewModel.findItem(itemId)

            DetailScreen(
                item = item,
                isFavorite = itemId in cafeUiState.favoriteItemIds,
                cartQuantity = cafeUiState.cartQuantities[itemId] ?: 0,
                isSaving = cafeUiState.isSaving,
                errorMessage = cafeUiState.errorMessage,
                onBack = { navController.popBackStack() },
                onFavoriteToggle = { cafeViewModel.toggleFavorite(itemId) },
                onAddToCart = { cafeViewModel.addToCart(itemId) },
                onEdit = { navController.navigate(Routes.edit(itemId)) },
                onDelete = {
                    cafeViewModel.deleteItem(itemId) {
                        navController.popBackStack()
                    }
                },
            )
        }

        composable(
            route = Routes.EDIT_PATTERN,
            arguments = listOf(navArgument(Routes.ITEM_ID) { type = NavType.StringType }),
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(Routes.ITEM_ID).orEmpty()
            val existingItem = cafeViewModel.findItem(itemId)

            AddEditItemScreen(
                existingItem = existingItem,
                isSaving = cafeUiState.isSaving,
                onBack = { navController.popBackStack() },
                onSave = { name, price, description, categoryId, available ->
                    if (existingItem != null) {
                        cafeViewModel.updateItem(
                            existingItem = existingItem,
                            name = name,
                            price = price,
                            description = description,
                            categoryId = categoryId,
                            available = available,
                            onSuccess = { navController.popBackStack() },
                        )
                    }
                },
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                user = authUiState.user,
                firebaseAuthEnabled = authUiState.isFirebaseEnabled,
                databaseEnabled = authUiState.isDatabaseEnabled,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
