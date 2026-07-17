package com.example.cafex.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cafex.model.CafeCategories
import com.example.cafex.model.CafeItem
import com.example.cafex.model.User
import com.example.cafex.ui.components.CafeLogo
import com.example.cafex.ui.components.MenuItemIcon
import com.example.cafex.viewmodel.CafeUiState
import java.util.Locale

private data class CategoryDashboardStat(
    val id: String,
    val name: String,
    val count: Int,
    val averagePrice: Double,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: User?,
    uiState: CafeUiState,
    onOpenMenu: () -> Unit,
    onAddItem: () -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onItemClick: (CafeItem) -> Unit,
    onMessageShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val totalItems = uiState.items.size
    val availableItems = uiState.items.count { it.available }
    val unavailableItems = totalItems - availableItems
    val favoriteCount = uiState.favoriteItemIds.count { favoriteId ->
        uiState.items.any { it.id == favoriteId }
    }
    val averagePrice = uiState.items.map { it.price }.average().takeUnless { it.isNaN() } ?: 0.0
    val highestPrice = uiState.items.maxOfOrNull { it.price } ?: 0.0
    val availabilityFraction = if (totalItems == 0) 0f else availableItems.toFloat() / totalItems
    val recentItems = uiState.items.sortedByDescending { it.createdAt }.take(4)
    val categoryStats = CafeCategories.all
        .filterNot { it.id == "all" }
        .map { category ->
            val categoryItems = uiState.items.filter { it.categoryId == category.id }
            CategoryDashboardStat(
                id = category.id,
                name = category.name,
                count = categoryItems.size,
                averagePrice = categoryItems.map { it.price }.average().takeUnless { it.isNaN() } ?: 0.0,
            )
        }
    val largestCategory = categoryStats.maxOfOrNull { it.count }?.coerceAtLeast(1) ?: 1

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
                title = {},
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
                            Icon(Icons.Rounded.ShoppingCart, contentDescription = "Open cart")
                        }
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Open profile")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Welcome back, ${user?.fullName?.substringBefore(' ').orEmpty().ifBlank { "CafeX owner" }}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Here’s a live overview of your café.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            CafeLogo(size = 68.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CafeX command center",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "$availableItems of $totalItems menu items are ready to serve.",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(onClick = onOpenMenu, modifier = Modifier.weight(1f)) {
                                Text("Open menu")
                            }
                            OutlinedButton(onClick = onAddItem, modifier = Modifier.weight(1f)) {
                                Text("Add item")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "At a glance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DashboardMetricCard(
                        title = "Menu items",
                        value = totalItems.toString(),
                        supporting = "$unavailableItems unavailable",
                        icon = Icons.Rounded.RestaurantMenu,
                        modifier = Modifier.weight(1f),
                    )
                    DashboardMetricCard(
                        title = "Average price",
                        value = String.format(Locale.US, "Rs. %.0f", averagePrice),
                        supporting = String.format(Locale.US, "High Rs. %.0f", highestPrice),
                        icon = Icons.AutoMirrored.Rounded.TrendingUp,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    DashboardMetricCard(
                        title = "Favorites",
                        value = favoriteCount.toString(),
                        supporting = "Saved this session",
                        icon = Icons.Rounded.Favorite,
                        modifier = Modifier.weight(1f),
                    )
                    DashboardMetricCard(
                        title = "Cart",
                        value = uiState.cartItemCount.toString(),
                        supporting = String.format(Locale.US, "Rs. %.0f total", uiState.cartTotal),
                        icon = Icons.Rounded.ShoppingCart,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "Menu availability",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "${(availabilityFraction * 100).toInt()}% ready to order",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape,
                                ),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(availabilityFraction.coerceIn(0f, 1f))
                                    .height(10.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.tertiary,
                                        shape = CircleShape,
                                    ),
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Category performance",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            items(categoryStats, key = { it.id }) { stat ->
                CategoryPerformanceCard(
                    stat = stat,
                    progress = stat.count.toFloat() / largestCategory,
                )
            }

            item {
                Text(
                    text = "Recently added",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (recentItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(Icons.Rounded.Inventory2, contentDescription = null)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Add your first menu item to populate the dashboard.")
                        }
                    }
                }
            } else {
                items(recentItems, key = { it.id }) { item ->
                    Card(
                        onClick = { onItemClick(item) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    MenuItemIcon(
                                        item = item,
                                        modifier = Modifier.size(26.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = if (item.available) "Available now" else "Unavailable",
                                    color = if (item.available) {
                                        MaterialTheme.colorScheme.tertiary
                                    } else {
                                        MaterialTheme.colorScheme.error
                                    },
                                )
                            }
                            Text(
                                text = String.format(Locale.US, "Rs. %.0f", item.price),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardMetricCard(
    title: String,
    value: String,
    supporting: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CategoryPerformanceCard(
    stat: CategoryDashboardStat,
    progress: Float,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    MenuItemIcon(
                        item = CafeItem(name = stat.name, categoryId = stat.id),
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(stat.name, fontWeight = FontWeight.Bold)
                    Text("${stat.count} items", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, CircleShape),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (stat.count == 0) {
                        "No items yet"
                    } else {
                        String.format(Locale.US, "Average Rs. %.0f", stat.averagePrice)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
