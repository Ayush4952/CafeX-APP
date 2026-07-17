package com.example.cafex.viewmodel

import com.example.cafex.model.CafeItem
import org.junit.Assert.assertEquals
import org.junit.Test

class CafeUiStateTest {
    private val coffee = CafeItem(
        id = "coffee",
        name = "Coffee",
        price = 300.0,
        categoryId = "coffee",
        available = true,
        createdAt = 3L,
    )
    private val tea = CafeItem(
        id = "tea",
        name = "Tea",
        price = 200.0,
        categoryId = "tea",
        available = true,
        createdAt = 2L,
    )
    private val cake = CafeItem(
        id = "cake",
        name = "Cake",
        price = 100.0,
        categoryId = "dessert",
        available = false,
        createdAt = 1L,
    )

    @Test
    fun visibleItemsCombinesFavoritesAvailabilityAndSorting() {
        val state = CafeUiState(
            items = listOf(coffee, tea, cake),
            favoriteItemIds = setOf(coffee.id, tea.id, cake.id),
            favoritesOnly = true,
            availableOnly = true,
            sortOrder = MenuSortOrder.PRICE_LOW_TO_HIGH,
        )

        assertEquals(listOf(tea.id, coffee.id), state.visibleItems.map { it.id })
    }

    @Test
    fun cartCalculatesQuantityLinesAndTotal() {
        val state = CafeUiState(
            items = listOf(coffee, tea, cake),
            cartQuantities = mapOf(coffee.id to 1, tea.id to 2),
        )

        assertEquals(3, state.cartItemCount)
        assertEquals(listOf(coffee.id, tea.id), state.cartLines.map { it.item.id })
        assertEquals(700.0, state.cartTotal, 0.001)
    }
}
