package com.example.cafex.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.Cake
import androidx.compose.material.icons.rounded.EmojiFoodBeverage
import androidx.compose.material.icons.rounded.Icecream
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.LunchDining
import androidx.compose.material.icons.rounded.RestaurantMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cafex.model.CafeItem

@Composable
fun MenuItemIcon(
    item: CafeItem,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        imageVector = iconForMenuItem(item),
        contentDescription = "${item.name} icon",
        modifier = modifier,
        tint = tint,
    )
}

private fun iconForMenuItem(item: CafeItem): ImageVector {
    val itemName = item.name.lowercase()

    return when {
        itemName.containsAny("ice cream", "gelato", "sundae") -> Icons.Rounded.Icecream
        itemName.containsAny("croissant", "bread", "muffin", "donut", "doughnut", "pastry", "bagel") -> {
            Icons.Rounded.BakeryDining
        }

        itemName.containsAny("cake", "brownie", "cookie", "dessert", "tiramisu", "cheesecake") -> {
            Icons.Rounded.Cake
        }

        itemName.containsAny("tea", "chai", "matcha") -> Icons.Rounded.EmojiFoodBeverage
        itemName.containsAny("sandwich", "burger", "wrap", "toast", "panini") -> Icons.Rounded.LunchDining
        itemName.containsAny("cold brew", "juice", "smoothie", "lemonade", "soda", "shake") -> {
            Icons.Rounded.LocalDrink
        }

        itemName.containsAny(
            "coffee",
            "cappuccino",
            "latte",
            "mocha",
            "espresso",
            "americano",
            "macchiato",
        ) -> Icons.Rounded.LocalCafe

        item.categoryId == "tea" -> Icons.Rounded.EmojiFoodBeverage
        item.categoryId == "bakery" -> Icons.Rounded.BakeryDining
        item.categoryId == "dessert" -> Icons.Rounded.Cake
        item.categoryId == "coffee" -> Icons.Rounded.LocalCafe
        else -> Icons.Rounded.RestaurantMenu
    }
}

private fun String.containsAny(vararg keywords: String): Boolean = keywords.any(::contains)
