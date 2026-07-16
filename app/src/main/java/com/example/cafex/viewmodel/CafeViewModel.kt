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

data class CafeUiState(
    val items: List<CafeItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String = "all",
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
) {
    val visibleItems: List<CafeItem>
        get() = items.filter { item ->
            val categoryMatches = selectedCategoryId == "all" ||
                item.categoryId == selectedCategoryId
            val queryMatches = searchQuery.isBlank() ||
                item.name.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true)
            categoryMatches && queryMatches
        }
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
                    _uiState.update { it.copy(items = items, isLoading = false) }
                }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
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
            onSuccess = onSuccess,
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
