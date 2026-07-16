package com.example.cafex.repository

import com.example.cafex.model.CafeItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseCafeRepository(
    database: DatabaseReference,
) : CafeRepository {
    private val itemsReference = database.child("items")

    override fun observeItems(): Flow<List<CafeItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children
                    .mapNotNull { it.getValue(CafeItem::class.java) }
                    .sortedByDescending(CafeItem::createdAt)
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        itemsReference.addValueEventListener(listener)
        awaitClose { itemsReference.removeEventListener(listener) }
    }

    override suspend fun addItem(item: CafeItem, userId: String): Result<Unit> = runCatching {
        val key = requireNotNull(itemsReference.push().key) { "Could not create an item id." }
        val storedItem = item.copy(
            id = key,
            createdBy = userId,
            createdAt = System.currentTimeMillis(),
        )
        itemsReference.child(key).setValue(storedItem).await()
    }

    override suspend fun updateItem(item: CafeItem): Result<Unit> = runCatching {
        require(item.id.isNotBlank()) { "The item id is missing." }
        itemsReference.child(item.id).setValue(item).await()
    }

    override suspend fun deleteItem(itemId: String): Result<Unit> = runCatching {
        require(itemId.isNotBlank()) { "The item id is missing." }
        itemsReference.child(itemId).removeValue().await()
    }
}
