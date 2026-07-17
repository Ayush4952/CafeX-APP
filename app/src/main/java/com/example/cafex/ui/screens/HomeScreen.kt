package com.example.cafex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SpaceDashboard
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cafex.model.CafeCategories
import com.example.cafex.model.CafeItem
import com.example.cafex.model.User
import com.example.cafex.ui.components.CafeItemCard
import com.example.cafex.ui.components.CafeLogo
import com.example.cafex.viewmodel.CafeUiState
import com.example.cafex.viewmodel.MenuSortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User?,
    uiState: CafeUiState,
    databaseEnabled: Boolean,
    onSearchChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSortOrderSelected: (MenuSortOrder) -> Unit,
    onAvailableOnlyToggle: () -> Unit,
    onFavoritesOnlyToggle: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onAddToCart: (String) -> Unit,
    onItemClick: (CafeItem) -> Unit,
    onAddClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val sortLabel = when (uiState.sortOrder) {
        MenuSortOrder.RECOMMENDED -> "Recommended"
        MenuSortOrder.NAME -> "Name"
        MenuSortOrder.PRICE_LOW_TO_HIGH -> "Lowest price"
        MenuSortOrder.PRICE_HIGH_TO_LOW -> "Highest price"
    }

    LaunchedEffect(uiState.errorMessage, uiState.infoMessage) {
        val message = uiState.errorMessage ?: uiState.infoMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onMessageShown()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { CafeLogo(size = 44.dp) },
                navigationIcon = {
                    IconButton(onClick = onDashboardClick) {
                        Icon(
                            imageVector = Icons.Rounded.SpaceDashboard,
                            contentDescription = "Open dashboard",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick) {
                        BadgedBox(
                            badge = {
                                if (uiState.cartItemCount > 0) {
                                    Badge {
                                        Text(uiState.cartItemCount.coerceAtMost(99).toString())
                                    }
                                }
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ShoppingCart,
                                contentDescription = "Open cart",
                            )
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Open profile",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("Add item") },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hello, ${user?.fullName?.substringBefore(' ').orEmpty().ifBlank { "coffee lover" }}",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = "What should we brew today?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!databaseEnabled) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Local menu — enable Realtime Database for cloud sync",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    leadingIcon = {
                        Icon(Icons.Rounded.Search, contentDescription = null)
                    },
                    placeholder = { Text("Search the menu") },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                ) {
                    items(CafeCategories.all, key = { it.id }) { category ->
                        FilterChip(
                            selected = uiState.selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) },
                            label = { Text(category.name) },
                        )
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp),
                ) {
                    item {
                        FilterChip(
                            selected = uiState.favoritesOnly,
                            onClick = onFavoritesOnlyToggle,
                            leadingIcon = {
                                Icon(Icons.Rounded.Favorite, contentDescription = null)
                            },
                            label = { Text("Favorites") },
                        )
                    }
                    item {
                        FilterChip(
                            selected = uiState.availableOnly,
                            onClick = onAvailableOnlyToggle,
                            leadingIcon = {
                                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                            },
                            label = { Text("Available") },
                        )
                    }
                    item {
                        Box {
                            FilterChip(
                                selected = uiState.sortOrder != MenuSortOrder.RECOMMENDED,
                                onClick = { sortMenuExpanded = true },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Rounded.Sort, contentDescription = null)
                                },
                                label = { Text(sortLabel) },
                            )
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false },
                            ) {
                                MenuSortOrder.entries.forEach { sortOrder ->
                                    val label = when (sortOrder) {
                                        MenuSortOrder.RECOMMENDED -> "Recommended"
                                        MenuSortOrder.NAME -> "Name"
                                        MenuSortOrder.PRICE_LOW_TO_HIGH -> "Lowest price"
                                        MenuSortOrder.PRICE_HIGH_TO_LOW -> "Highest price"
                                    }
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            onSortOrderSelected(sortOrder)
                                            sortMenuExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Text(
                        text = "Loading the menu…",
                        modifier = Modifier.padding(vertical = 28.dp),
                    )
                }
            } else if (uiState.visibleItems.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "No menu items found",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Try another search or add your first item.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            } else {
                items(uiState.visibleItems, key = { it.id }) { item ->
                    CafeItemCard(
                        item = item,
                        isFavorite = item.id in uiState.favoriteItemIds,
                        cartQuantity = uiState.cartQuantities[item.id] ?: 0,
                        onClick = { onItemClick(item) },
                        onToggleFavorite = { onFavoriteToggle(item.id) },
                        onAddToCart = { onAddToCart(item.id) },
                    )
                }
            }
        }
    }
}
