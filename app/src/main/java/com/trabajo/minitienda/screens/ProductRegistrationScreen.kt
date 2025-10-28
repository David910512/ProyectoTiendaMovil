package com.trabajo.minitienda.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.data.database.AppDbProvider
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.components.PrimaryButton
import com.trabajo.minitienda.ui.theme.PrimaryGreen
import com.trabajo.minitienda.ui.theme.SecondaryText
import com.trabajo.minitienda.viewmodel.CategoryViewModel
import com.trabajo.minitienda.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExposedDropdownMenuBox


import androidx.compose.material3.DropdownMenuItem

// Dropdown expuesto
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductRegistrationScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    categoryViewModel: CategoryViewModel
) {
    // ---- estado de formulario ----
    var nombre by rememberSaveable { mutableStateOf("") }
    var codigo by rememberSaveable { mutableStateOf("") }
    var precioTxt by rememberSaveable { mutableStateOf("") }
    var stockTxt by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

    // categoría (texto que escribe/selecciona el usuario)
    var categoriaNombre by rememberSaveable { mutableStateOf("") }
    var catExpanded by remember { mutableStateOf(false) }




    val precio = remember(precioTxt) { precioTxt.replace(',', '.').trim().toDoubleOrNull() }
    val stock  = remember(stockTxt) { stockTxt.trim().toIntOrNull() }
    val esErrorPrecio = precioTxt.isNotBlank() && precio == null
    val esErrorStock  = stockTxt.isNotBlank() && stock == null

    // ---- infra ----
    val ctx = LocalContext.current
    val productoDao = remember { AppDbProvider.get(ctx).productDao() }
    val categoriaDao = remember { AppDbProvider.get(ctx).categoryDao() }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // categorías desde VM (reactivo)
    var showCatManager by remember { mutableStateOf(false) }
    val categories by categoryViewModel.categories.collectAsState()

    PageLayout(title = "Nuevo Producto", onMenuClick = { }) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                AppCard(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Información del Producto", style = MaterialTheme.typography.titleMedium)

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                            TextField(
                                value = nombre,
                                onValueChange = { nombre = it },
                                label = { Text("Nombre del Producto") },
                                placeholder = { Text("Ej: Arroz Extra") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = true,
                                    capitalization = KeyboardCapitalization.Sentences
                                )
                            )

                            TextField(
                                value = codigo,
                                onValueChange = { codigo = it },
                                label = { Text("Código") },
                                placeholder = { Text("Ej: P001") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false,
                                    capitalization = KeyboardCapitalization.Characters
                                )
                            )

                            // -------- CATEGORÍA (selector con autocompletar) --------
                            // -- CATEGORÍA (selector + crear/administrar) --
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Categoría", style = MaterialTheme.typography.titleSmall)

                                ExposedDropdownMenuBox(
                                    expanded = catExpanded,
                                    onExpandedChange = { catExpanded = !catExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = categoriaNombre,
                                        onValueChange = { categoriaNombre = it },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        label = { Text("Selecciona o escribe…") },
                                        placeholder = { Text("Ej: Abarrotes") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded)
                                        },
                                        singleLine = true
                                    )

                                    ExposedDropdownMenu(
                                        expanded = catExpanded,
                                        onDismissRequest = { catExpanded = false }
                                    ) {
                                        categories.forEach { c ->
                                            DropdownMenuItem(
                                                text = { Text(c.nombre) },
                                                onClick = {
                                                    categoriaNombre = c.nombre
                                                    catExpanded = false
                                                }
                                            )
                                        }

                                        // Sugerir creación si el texto no existe aún
                                        val texto = categoriaNombre.trim()
                                        if (texto.isNotEmpty() &&
                                            categories.none { it.nombre.equals(texto, ignoreCase = true) }
                                        ) {
                                            Divider()
                                            DropdownMenuItem(
                                                text = { Text("➕ Crear “$texto”") },
                                                onClick = {
                                                    catExpanded = false
                                                    scope.launch { categoryViewModel.add(texto) } // crea rápido
                                                }
                                            )
                                        }
                                    }
                                }

                                // Botón para CRUD completo en diálogo
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showCatManager = true }) {
                                        Text("Administrar categorías")
                                    }
                                }
                            }

                            // --------------------------------------------------------

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TextField(
                                    value = precioTxt,
                                    onValueChange = { precioTxt = it },
                                    label = { Text("Precio (S/)") },
                                    placeholder = { Text("0.00") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    isError = esErrorPrecio,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next
                                    )
                                )

                                TextField(
                                    value = stockTxt,
                                    onValueChange = { stockTxt = it },
                                    label = { Text("Stock Inicial") },
                                    placeholder = { Text("0") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    isError = esErrorStock,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    )
                                )
                            }

                            TextField(
                                value = descripcion,
                                onValueChange = { descripcion = it },
                                label = { Text("Descripción") },
                                placeholder = { Text("Describe el producto...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                singleLine = false
                            )
                        }
                    }
                }

                ImageUploadSection()

                // --------- Acciones ----------
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    PrimaryButton(
                        text = "Guardar Producto",
                        onClick = {
                            val ok = nombre.isNotBlank() && codigo.isNotBlank() && precio != null && stock != null
                            if (!ok) {
                                scope.launch { snackbar.showSnackbar("Completa todos los campos con valores válidos") }
                                return@PrimaryButton
                            }

                            scope.launch {
                                try {
                                    // Resolver categoryId (si escribió/seleccionó algo)
                                    var catId: Long? = null
                                    val texto = categoriaNombre.trim()
                                    if (texto.isNotEmpty()) {
                                        // ¿existe ya en la lista?
                                        val existente = categories.firstOrNull {
                                            it.nombre.equals(texto, ignoreCase = true)
                                        }
                                        if (existente != null) {
                                            catId = existente.id
                                        } else {
                                            // crear y obtener id (upsert por nombre)
                                            val newId = categoriaDao.upsertByName(texto)
                                            catId = if (newId != -1L) newId else categories
                                                .firstOrNull { it.nombre.equals(texto, ignoreCase = true) }?.id
                                        }
                                    }

                                    val p = Product(
                                        name = nombre.trim(),
                                        code = codigo.trim().uppercase(),
                                        price = precio!!,
                                        stock = stock!!,
                                        descripcion = descripcion.trim(),
                                        categoryId = catId
                                    )

                                    // Guarda (upsert por code)
                                    productoDao.upsertByCode(p)

                                    snackbar.showSnackbar("Producto guardado")
                                    navController.navigateUp()
                                } catch (e: Exception) {
                                    snackbar.showSnackbar("Error al guardar: ${e.message ?: "desconocido"}")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            CategoryManagerDialog(
                show = showCatManager,
                onDismiss = { showCatManager = false },
                categoryViewModel = categoryViewModel
            )

            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp)
            )
        }
    }
}

@Composable
private fun ImageUploadSection() {
    AppCard(Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(
                        width = 2.dp,
                        color = PrimaryGreen.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Haz clic para seleccionar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = "Formatos soportados: JPG, PNG",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun CategoryManagerDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    categoryViewModel: CategoryViewModel
) {
    if (!show) return

    val cats by categoryViewModel.categories.collectAsState()
    var nuevoNombre by rememberSaveable { mutableStateOf("") }
    var toDeleteId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = { Text("Categorías") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = { nuevoNombre = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Nueva categoría") },
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (nuevoNombre.isNotBlank()) {
                                categoryViewModel.add(nuevoNombre)
                                nuevoNombre = ""
                            }
                        }
                    ) { Text("Agregar") }
                }

                Divider()

                if (cats.isEmpty()) {
                    Text("Aún no hay categorías", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(cats, key = { it.id }) { c ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(c.nombre, style = MaterialTheme.typography.bodyMedium)
                                IconButton(onClick = { toDeleteId = c.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )

    // Confirmación de borrado
    if (toDeleteId != null) {
        AlertDialog(
            onDismissRequest = { toDeleteId = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Seguro que deseas eliminarla?") },
            confirmButton = {
                TextButton(onClick = {
                    categoryViewModel.delete(toDeleteId!!)
                    toDeleteId = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { toDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}

