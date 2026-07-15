package com.carpes.planeschase.ui.menu

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import com.carpes.planeschase.data.local.entity.DeckEntity
import com.carpes.planeschase.ui.planes.AddPlaneDialog

@Composable
fun MainMenuScreen(
    onNavigateToPlanes: (Int?) -> Unit,
    onNavigateToGallery: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { PlaneschaseDatabase.getInstance(context) }
    val allDecks by db.deckDao().getAllDecks().collectAsState(initial = emptyList())
    
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    var showAddPlaneDialog by remember { mutableStateOf(false) }
    var showDeckSelection by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Planechase",
            style = if (isLandscape) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 64.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MenuButton(
                text = "Ver planos",
                icon = Icons.Default.PlayArrow,
                onClick = { showDeckSelection = true }
            )

            MenuButton(
                text = "Galería y Mazos",
                icon = Icons.Default.Collections,
                onClick = onNavigateToGallery
            )

            MenuButton(
                text = "Añadir carta",
                icon = Icons.Default.Add,
                onClick = { showAddPlaneDialog = true }
            )
        }
    }

    if (showAddPlaneDialog) {
        AddPlaneDialog(
            onDismiss = { showAddPlaneDialog = false },
            onPlaneAdded = { showAddPlaneDialog = false }
        )
    }
    
    if (showDeckSelection) {
        AlertDialog(
            onDismissRequest = { showDeckSelection = false },
            title = { Text("Selecciona un mazo") },
            text = {
                LazyColumn {
                    item {
                        ListItem(
                            headlineContent = { Text("Mazo Aleatorio (Todos los planos)") },
                            modifier = Modifier.clickable {
                                showDeckSelection = false
                                onNavigateToPlanes(null)
                            }
                        )
                    }
                    items(allDecks) { deck ->
                        ListItem(
                            headlineContent = { Text(deck.name) },
                            modifier = Modifier.clickable {
                                showDeckSelection = false
                                onNavigateToPlanes(deck.id)
                            }
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDeckSelection = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(250.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(text = text)
        }
    }
}
