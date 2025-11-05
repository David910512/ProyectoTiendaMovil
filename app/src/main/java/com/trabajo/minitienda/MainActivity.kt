package com.trabajo.minitienda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.trabajo.minitienda.screens.MainNavigation
import com.trabajo.minitienda.ui.theme.MiniTiendaTheme
import com.trabajo.minitienda.utils.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Crea e inicializa el ThemeManager
            val themeManager = remember { ThemeManager(this) }
            val isDarkTheme by themeManager.isDarkTheme.collectAsState()

            // Pasa el estado del tema
            MiniTiendaTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pasa el themeManager a la navegación
                    MainNavigation(themeManager = themeManager)
                }
            }
        }
    }
}
