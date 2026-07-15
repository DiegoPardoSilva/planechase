package com.carpes.planeschase.ui.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import com.carpes.planeschase.data.local.entity.DeckEntity
import com.carpes.planeschase.data.local.entity.DeckPlaneCrossRef
import com.carpes.planeschase.data.local.entity.PlaneEntity
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { PlaneschaseDatabase.getInstance(context) }
    
    val allPlanes by db.planeDao().getAllPlanes().collectAsState(initial = emptyList())
    val allDecks by db.deckDao().getAllDecks().collectAsState(initial = emptyList())
    
    var selectedDeck by remember { mutableStateOf<DeckEntity?>(null) }
    val planesInDeck by if (selectedDeck != null) {
        db.deckDao().getPlanesInDeck(selectedDeck!!.id).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList<PlaneEntity>()) }
    }

    var showCreateDeckDialog by remember { mutableStateOf(false) }
    var zoomPlane by remember { mutableStateOf<PlaneEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedDeck?.name ?: "Galería de Planos", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedDeck != null) selectedDeck = null else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (selectedDeck == null) {
                        IconButton(onClick = { showCreateDeckDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Crear mazo", tint = Color.White)
                        }
                    } else {
                        IconButton(onClick = {
                            scope.launch {
                                db.deckDao().deleteDeck(selectedDeck!!)
                                selectedDeck = null
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Borrar mazo", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        if (selectedDeck == null) {
            Column(modifier = Modifier.padding(padding)) {
                if (allDecks.isNotEmpty()) {
                    Text("Mis Mazos", color = Color.White, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .horizontalScroll(rememberScrollState())
                    ) {
                        allDecks.forEach { deck ->
                            DeckChip(deck, onClick = { selectedDeck = deck })
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                
                Text("Todos los Planos", color = Color.White, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.titleMedium)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allPlanes) { plane ->
                        PlaneGalleryItem(
                            plane = plane, 
                            isSelected = false, 
                            onClick = { zoomPlane = plane }
                        )
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allPlanes) { plane ->
                    val isInDeck = planesInDeck.any { it.id == plane.id }
                    PlaneGalleryItem(
                        plane = plane, 
                        isSelected = isInDeck, 
                        onClick = {
                            scope.launch {
                                if (isInDeck) {
                                    db.deckDao().deleteDeckPlaneCrossRef(DeckPlaneCrossRef(selectedDeck!!.id, plane.id))
                                } else {
                                    db.deckDao().insertDeckPlaneCrossRef(DeckPlaneCrossRef(selectedDeck!!.id, plane.id))
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showCreateDeckDialog) {
        var deckName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCreateDeckDialog = false },
            title = { Text("Nuevo Mazo") },
            text = {
                OutlinedTextField(value = deckName, onValueChange = { deckName = it }, label = { Text("Nombre del mazo") })
            },
            confirmButton = {
                TextButton(onClick = {
                    if (deckName.isNotBlank()) {
                        scope.launch {
                            db.deckDao().insertDeck(DeckEntity(name = deckName))
                            showCreateDeckDialog = false
                        }
                    }
                }) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDeckDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (zoomPlane != null) {
        Dialog(
            onDismissRequest = { zoomPlane = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { zoomPlane = null },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = File(zoomPlane!!.image).takeIf { it.exists() },
                    contentDescription = zoomPlane!!.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { zoomPlane = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun DeckChip(deck: DeckEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(deck.name, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
fun PlaneGalleryItem(plane: PlaneEntity, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.DarkGray)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = File(plane.image).takeIf { it.exists() },
                contentDescription = plane.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alpha = if (isSelected) 0.5f else 1f
            )
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.align(Alignment.Center), tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .align(Alignment.BottomCenter)
                    .padding(2.dp)
            ) {
                Text(
                    text = plane.name,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1
                )
            }
        }
    }
}
