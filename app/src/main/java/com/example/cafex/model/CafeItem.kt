package com.example.cafex.model

data class CafeItem(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val categoryId: String = "coffee",
    val available: Boolean = true,
    val createdBy: String = "",
    val createdAt: Long = 0L,
)
