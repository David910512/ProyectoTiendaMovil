package com.trabajo.minitienda.data

import com.trabajo.minitienda.data.model.Product

// Simple shared mock models and data used only for static UI previews

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
    Product(1, "Arroz Extra", "PRD001", 4.50, 50, "Produ"),
    Product(2, "Aceite Vegetal 1L", "PRD002", 8.90, 30,"Produ"),
    Product(3, "Leche Gloria 400g", "PRD003", 3.80, 8,"Prodcuto"),
    Product(4, "Azúcar Rubia 1kg", "PRD004", 4.20, 0,"Procucto "),
    Product(5, "Fideos Don Vittorio", "PRD005", 2.50, 45,"Producto"),
    Product(6, "Atún Real", "PRD006", 5.50, 25,"Producto"),
    Product(7, "Papel Higiénico", "PRD007", 12.90, 15,"Producto"),
    Product(8, "Jabón Protex", "PRD008", 2.80, 5,"Producto"),
    Product(9, "Detergente Ace 1kg", "PRD009", 15.90, 20,"Producto"),
    Product(10, "Galletas Soda", "PRD010", 1.50, 60,"Producto")
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
