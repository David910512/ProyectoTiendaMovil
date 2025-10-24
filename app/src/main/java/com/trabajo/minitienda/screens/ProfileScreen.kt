package com.trabajo.minitienda.screens

import androidx.compose.foundation.background
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
import com.trabajo.minitienda.ui.components.*
import com.trabajo.minitienda.ui.theme.*

@Composable
fun ProfileScreen(navController: NavController) {
    PageLayout(
        title = "Perfil",
        onMenuClick = { /* TODO: Open drawer */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera de perfil
            ProfileHeader()
            
            // Información de la tienda
            StoreInfoCard()
            
            // Configuraciones
            SettingsCard()
            
            // Botón de cerrar sesión
            AppCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /* TODO */ },
                    colors = buttonColors(
                        containerColor = ErrorColor
                    ),
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
private fun ProfileHeader() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CardBackground,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // Información del usuario
            Column {
                Text(
                    text = "Juan Pérez",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Administrador",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText
                )
                Text(
                    text = "juan.perez@example.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun StoreInfoCard() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información de la Tienda",
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.Store,
                    label = "Nombre",
                    value = "Bodega La Esquina"
                )
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Dirección",
                    value = "Av. Lima 123, San Miguel"
                )
                InfoRow(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = "(01) 555-1234"
                )
                InfoRow(
                    icon = Icons.Default.Numbers,
                    label = "RUC",
                    value = "20123456789"
                )
            }

            OutlinedButton(
                onClick = { /* TODO */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Información")
            }
        }
    }
}

@Composable
private fun SettingsCard() {
    AppCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.titleMedium
            )
            
            Column {
                SettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    subtitle = "Alertas de stock bajo y ventas"
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.Security,
                    title = "Seguridad",
                    subtitle = "Contraseña y autenticación"
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.Print,
                    title = "Impresión",
                    subtitle = "Configurar impresora de tickets"
                )
                Divider()
                SettingItem(
                    icon = Icons.Default.Language,
                    title = "Idioma",
                    subtitle = "Español (Perú)"
                )
            }
        }
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
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
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
            tint = PrimaryGreen,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = SecondaryText
        )
    }
}