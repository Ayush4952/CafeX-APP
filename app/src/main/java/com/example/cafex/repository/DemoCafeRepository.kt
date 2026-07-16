package com.example.cafex.repository

import com.example.cafex.model.CafeItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class DemoCafeRepository : CafeRepository {
    private val items = MutableStateFlow(sampleItems())

    override fun observeItems(): Flow<List<CafeItem>> = items.asStateFlow()

    override suspend fun addItem(item: CafeItem, userId: String): Result<Unit> = runCatching {
        delay(250)
        val storedItem = item.copy(
            id = UUID.randomUUID().toString(),
            createdBy = userId,
            createdAt = System.currentTimeMillis(),
        )
        items.update { listOf(storedItem) + it }
    }

    override suspend fun updateItem(item: CafeItem): Result<Unit> = runCatching {
        delay(250)
        items.update { current -> current.map { if (it.id == item.id) item else it } }
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        delay(200)
        items.update { current -> current.filterNot { it.id == itemId } }
    }

    private fun sampleItems() = listOf(
        CafeItem(
            id = "demo-cappuccino",
            name = "Velvet Cappuccino",
            description = "Double espresso, steamed milk and a cloud of microfoam.",
            price = 260.0,
            categoryId = "coffee",
            available = true,
            createdBy = "demo-user",
            createdAt = 3L,
        ),
        CafeItem(
            id = "demo-cold-brew",
            name = "Vanilla Cold Brew",
            description = "Slow-steeped coffee with vanilla cream over ice.",
            price = 310.0,
            categoryId = "coffee",
            available = true,
            createdBy = "demo-user",
            createdAt = 2L,
        ),
        CafeItem(
            id = "demo-croissant",
            name = "Butter Croissant",
            description = "Flaky, golden and baked fresh every morning.",
            price = 190.0,
            categoryId = "bakery",
            available = true,
            createdBy = "demo-user",
            createdAt = 1L,
        ),
    )
}
