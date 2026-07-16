package com.example.cafex.repository

import com.example.cafex.model.CafeItem
import kotlinx.coroutines.flow.Flow

interface CafeRepository {
    fun observeItems(): Flow<List<CafeItem>>

    suspend fun addItem(item: CafeItem, userId: String): Result<Unit>

    suspend fun updateItem(item: CafeItem): Result<Unit>

    suspend fun deleteItem(itemId: String): Result<Unit>
}
