package com.example.cafex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cafex.model.CafeItem
import com.example.cafex.repository.CafeRepository
import com.example.cafex.utils.Validation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MenuSortOrder {
    RECOMMENDED,
    NAME,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
}

data class CartLine(
    val item: CafeItem,
    val quantity: Int,
) {
    val subtotal: Double
        get() = item.price * quantity
}

data class CafeUiState(
    val items: List<CafeItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String = "all",
    val sortOrder: MenuSortOrder = MenuSortOrder.RECOMMENDED,
    val availableOnly: Boolean = false,
    val favoritesOnly: Boolean = false,
    val favoriteItemIds: Set<String> = emptySet(),
    val cartQuantities: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
) {
    val visibleItems: List<CafeItem>
        get() {
            val filteredItems = items.filter { item ->
                val categoryMatches = selectedCategoryId == "all" ||
                    item.categoryId == selectedCategoryId
                val queryMatches = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)
                val availabilityMatches = !availableOnly || item.available
                val favoriteMatches = !favoritesOnly || item.id in favoriteItemIds
                categoryMatches && queryMatches && availabilityMatches && favoriteMatches
            }

            return when (sortOrder) {
                MenuSortOrder.RECOMMENDED -> filteredItems.sortedWith(
                    compareByDescending<CafeItem> { it.available }
                        .thenByDescending { it.createdAt },
                )

                MenuSortOrder.NAME -> filteredItems.sortedBy { it.name.lowercase() }
                MenuSortOrder.PRICE_LOW_TO_HIGH -> filteredItems.sortedBy { it.price }
                MenuSortOrder.PRICE_HIGH_TO_LOW -> filteredItems.sortedByDescending { it.price }
            }
        }

    val cartLines: List<CartLine>
        get() = cartQuantities.mapNotNull { (itemId, quantity) ->
            items.find { it.id == itemId }?.let { item -> CartLine(item, quantity) }
        }.sortedBy { it.item.name.lowercase() }

    val cartItemCount: Int
        get() = cartQuantities.values.sum()

    val cartTotal: Double
        get() = cartLines.sumOf { it.subtotal }
}

class CafeViewModel(
    private val repository: CafeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CafeUiState())
    val uiState: StateFlow<CafeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeItems()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Unable to load the menu.",
                        )
                    }
                }
                .collect { items ->
                    _uiState.update { state ->
                        val currentIds = items.mapTo(mutableSetOf()) { it.id }
                        state.copy(
                            items = items,
                            favoriteItemIds = state.favoriteItemIds.intersect(currentIds),
                            cartQuantities = state.cartQuantities.filterKeys { it in currentIds },
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun setSortOrder(sortOrder: MenuSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    fun toggleAvailableOnly() {
        _uiState.update { it.copy(availableOnly = !it.availableOnly) }
    }

    fun toggleFavoritesOnly() {
        _uiState.update { it.copy(favoritesOnly = !it.favoritesOnly) }
    }

    fun toggleFavorite(itemId: String) {
        _uiState.update { state ->
            val updatedFavorites = state.favoriteItemIds.toMutableSet().apply {
                if (!add(itemId)) remove(itemId)
            }
            state.copy(favoriteItemIds = updatedFavorites)
        }
    }

    fun addToCart(itemId: String) {
        val item = findItem(itemId) ?: return
        if (!item.available) {
            showError("${item.name} is currently unavailable.")
            return
        }

        _uiState.update { state ->
            val currentQuantity = state.cartQuantities[itemId] ?: 0
            state.copy(
                cartQuantities = state.cartQuantities + (itemId to (currentQuantity + 1).coerceAtMost(99)),
                errorMessage = null,
                infoMessage = "${item.name} added to your cart.",
            )
        }
    }

    fun increaseCartQuantity(itemId: String) {
        addToCart(itemId)
    }

    fun decreaseCartQuantity(itemId: String) {
        _uiState.update { state ->
            val currentQuantity = state.cartQuantities[itemId] ?: return@update state
            val updatedCart = if (currentQuantity <= 1) {
                state.cartQuantities - itemId
            } else {
                state.cartQuantities + (itemId to (currentQuantity - 1))
            }
            state.copy(cartQuantities = updatedCart)
        }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartQuantities = emptyMap()) }
    }

    fun placeOrder(onSuccess: () -> Unit) {
        if (_uiState.value.cartQuantities.isEmpty()) {
            showError("Add an item before placing an order.")
            return
        }

        _uiState.update {
            it.copy(
                cartQuantities = emptyMap(),
                errorMessage = null,
                infoMessage = "Order placed — your CafeX order is being prepared.",
            )
        }
        onSuccess()
    }

    fun findItem(itemId: String): CafeItem? = _uiState.value.items.find { it.id == itemId }

    fun addItem(
        name: String,
        price: String,
        description: String,
        categoryId: String,
        available: Boolean,
        userId: String,
        onSuccess: () -> Unit,
    ) {
        val validationError = Validation.itemError(name, price, description)
        if (validationError != null) {
            showError(validationError)
            return
        }

        val item = CafeItem(
            name = name.trim(),
            description = description.trim(),
            price = price.toDouble(),
            categoryId = categoryId,
            available = available,
        )

        save(
            action = { repository.addItem(item, userId) },
            successMessage = "Menu item added.",
            onSuccess = {
                _uiState.update {
                    it.copy(
                        searchQuery = "",
                        selectedCategoryId = categoryId,
                    )
                }
                onSuccess()
            },
        )
    }

    fun updateItem(
        existingItem: CafeItem,
        name: String,
        price: String,
        description: String,
        categoryId: String,
        available: Boolean,
        onSuccess: () -> Unit,
    ) {
        val validationError = Validation.itemError(name, price, description)
        if (validationError != null) {
            showError(validationError)
            return
        }

        val item = existingItem.copy(
            name = name.trim(),
            description = description.trim(),
            price = price.toDouble(),
            categoryId = categoryId,
            available = available,
        )

        save(
            action = { repository.updateItem(item) },
            successMessage = "Menu item updated.",
            onSuccess = onSuccess,
        )
    }

    fun deleteItem(itemId: String, onSuccess: () -> Unit) {
        save(
            action = { repository.deleteItem(itemId) },
            successMessage = "Menu item deleted.",
            onSuccess = onSuccess,
        )
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun save(
        action: suspend () -> Result<Unit>,
        successMessage: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }
            action().fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, infoMessage = successMessage) }
                    onSuccess()
                },
                onFailure = { error ->
                    showError(error.localizedMessage ?: "The operation failed.")
                },
            )
        }
    }

    private fun showError(message: String) {
        _uiState.update { it.copy(isSaving = false, errorMessage = message, infoMessage = null) }
    }
}
