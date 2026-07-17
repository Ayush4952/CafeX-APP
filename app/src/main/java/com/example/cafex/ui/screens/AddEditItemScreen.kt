package com.example.cafex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cafex.model.CafeCategories
import com.example.cafex.model.CafeItem
import com.example.cafex.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditItemScreen(
    existingItem: CafeItem?,
    isSaving: Boolean,
    onBack: () -> Unit,
    onSave: (String, String, String, String, Boolean) -> Unit,
    initialCategoryId: String = "coffee",
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable(existingItem?.id) {
        mutableStateOf(existingItem?.name.orEmpty())
    }
    var price by rememberSaveable(existingItem?.id) {
        mutableStateOf(existingItem?.price?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var description by rememberSaveable(existingItem?.id) {
        mutableStateOf(existingItem?.description.orEmpty())
    }
    var categoryId by rememberSaveable(existingItem?.id, initialCategoryId) {
        mutableStateOf(existingItem?.categoryId ?: initialCategoryId)
    }
    var available by rememberSaveable(existingItem?.id) {
        mutableStateOf(existingItem?.available ?: true)
    }

    androidx.compose.foundation.layout.Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(if (existingItem == null) "Add menu item" else "Edit menu item")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Go back")
                        }
                    },
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { value ->
                        price = value.filter { it.isDigit() || it == '.' }
                    },
                    label = { Text("Price (Rs.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                )

                Text("Category")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CafeCategories.all.drop(1), key = { it.id }) { category ->
                        FilterChip(
                            selected = categoryId == category.id,
                            onClick = { categoryId = category.id },
                            label = { Text(category.name) },
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Available")
                        Text("Customers can see this status")
                    }
                    Switch(checked = available, onCheckedChange = { available = it })
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { onSave(name, price, description, categoryId, available) },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                ) {
                    Text(if (existingItem == null) "Add to menu" else "Save changes")
                }
            }
        }

        LoadingOverlay(visible = isSaving)
    }
}
