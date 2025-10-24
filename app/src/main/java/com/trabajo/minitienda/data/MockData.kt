package com.trabajo.minitienda.data

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

// Simple shared mock models and data used only for static UI previews
data class Product(
    val name: String,
    val code: String,
    val price: Double,
    val stock: Int
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

enum class PurchaseStatus { COMPLETED, PENDING }

data class Purchase(
    val supplier: String,
    val invoiceNumber: String,
    val date: String,
    val items: Int,
    val total: Double,
    val status: PurchaseStatus,
    val dueDate: String? = null
)

val mockProducts = listOf(
    Product("Arroz Extra", "PRD001", 4.50, 50),
    Product("Aceite Vegetal 1L", "PRD002", 8.90, 30),
    Product("Leche Gloria 400g", "PRD003", 3.80, 8),
    Product("Azúcar Rubia 1kg", "PRD004", 4.20, 0),
    Product("Fideos Don Vittorio", "PRD005", 2.50, 45),
    Product("Atún Real", "PRD006", 5.50, 25),
    Product("Papel Higiénico", "PRD007", 12.90, 15),
    Product("Jabón Protex", "PRD008", 2.80, 5),
    Product("Detergente Ace 1kg", "PRD009", 15.90, 20),
    Product("Galletas Soda", "PRD010", 1.50, 60)
)

val mockPurchases = listOf(
    Purchase(
        supplier = "Distribuidora López",
        invoiceNumber = "F001-12345",
        date = "23 Oct 2025",
        items = 15,
        total = 2850.00,
        status = PurchaseStatus.PENDING,
        dueDate = "30 Oct 2025"
    ),
    Purchase(
        supplier = "Comercial García",
        invoiceNumber = "F002-98765",
        date = "22 Oct 2025",
        items = 8,
        total = 1250.00,
        status = PurchaseStatus.COMPLETED
    )
)

// Example static cart for Sales screen (no logic)
val mockCart = listOf(
    CartItem(product = mockProducts[0], quantity = 2),
    CartItem(product = mockProducts[2], quantity = 1)
)
