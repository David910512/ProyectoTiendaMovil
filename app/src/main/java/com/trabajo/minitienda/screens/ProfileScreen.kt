package com.trabajo.minitienda.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.trabajo.minitienda.ui.components.AppCard
import com.trabajo.minitienda.ui.components.PageLayout
import com.trabajo.minitienda.ui.theme.*
import com.trabajo.minitienda.utils.ThemeManager

@Composable
fun ProfileScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    themeManager: ThemeManager
) {
    val isDarkTheme by themeManager.isDarkTheme.collectAsState()

    PageLayout(
        title = "Perfil",
        onMenuClick = onMenuClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera del perfil editable
            ProfileHeaderEditable()

            // Información de la tienda editable
            StoreInfoCardEditable()

            // Configuraciones (CON SWITCH DE TEMA)
            SettingsSection(
                isDarkTheme = isDarkTheme,
                onThemeToggle = { themeManager.toggleTheme() }
            )

            // Botón de cerrar sesión
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    },
                    colors = buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar Sesión")
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderEditable() {
    var editing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Juan Pérez") }
    var role by remember { mutableStateOf("Administrador") }
    var email by remember { mutableStateOf("juan.perez@example.com") }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (editing) {
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre") },
                            singleLine = true
                        )
                        TextField(
                            value = role,
                            onValueChange = { role = it },
                            label = { Text("Rol") },
                            singleLine = true
                        )
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true
                        )
                    } else {
                        Text(text = name, style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = role,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { editing = !editing },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = if (editing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (editing) "Guardar" else "Editar")
            }
        }
    }
}

@Composable
private fun StoreInfoCardEditable() {
    var editing by remember { mutableStateOf(false) }
    var storeName by remember { mutableStateOf("Bodega La Esquina") }
    var address by remember { mutableStateOf("Av. Lima 123, San Miguel") }
    var phone by remember { mutableStateOf("(01) 555-1234") }
    var ruc by remember { mutableStateOf("20123456789") }

    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Información de la Tienda", style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (editing) {
                    EditableInfoRow(Icons.Default.Store, "Nombre", storeName) { storeName = it }
                    EditableInfoRow(Icons.Default.LocationOn, "Dirección", address) { address = it }
                    EditableInfoRow(Icons.Default.Phone, "Teléfono", phone) { phone = it }
                    EditableInfoRow(Icons.Default.Numbers, "RUC", ruc) { ruc = it }
                } else {
                    InfoRow(Icons.Default.Store, "Nombre", storeName)
                    InfoRow(Icons.Default.LocationOn, "Dirección", address)
                    InfoRow(Icons.Default.Phone, "Teléfono", phone)
                    InfoRow(Icons.Default.Numbers, "RUC", ruc)
                }
            }

            OutlinedButton(
                onClick = { editing = !editing },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = if (editing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (editing) "Guardar" else "Editar Información")
            }
        }
    }
}

@Composable
private fun EditableInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SettingsSection(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Configuraciones", style = MaterialTheme.typography.titleMedium)

            SettingItem(Icons.Default.Notifications, "Notificaciones", "Activar o desactivar alertas")

            // ITEM DE TEMA CON SWITCH FUNCIONAL
            ThemeSettingItem(
                isDarkTheme = isDarkTheme,
                onThemeToggle = onThemeToggle
            )

            SettingItem(Icons.Default.Language, "Idioma", "Español (predeterminado)")
        }
    }
}

@Composable
private fun ThemeSettingItem(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onThemeToggle() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ColorLens,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Tema", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = if (isDarkTheme) "Modo oscuro activado" else "Modo claro activado",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Switch(
            checked = isDarkTheme,
            onCheckedChange = { onThemeToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SettingItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}