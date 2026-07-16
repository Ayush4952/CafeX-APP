package com.example.cafex.model

data class Category(
    val id: String = "",
    val name: String = "",
)

object CafeCategories {
    val all = listOf(
        Category(id = "all", name = "All"),
        Category(id = "coffee", name = "Coffee"),
        Category(id = "tea", name = "Tea"),
        Category(id = "bakery", name = "Bakery"),
        Category(id = "dessert", name = "Dessert"),
    )
}
