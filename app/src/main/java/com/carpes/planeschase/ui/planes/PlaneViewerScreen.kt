package com.carpes.planeschase.ui.planes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random

@Composable
fun PlaneViewerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    
    val dbPlanes by remember {
        com.carpes.planeschase.data.local.PlaneschaseDatabase.getInstance(context).planeDao().getAllPlanes()
    }.collectAsState(initial = emptyList())

    var activePoolIds by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var timedOut by remember { mutableStateOf(false) }

    val activePool = remember(dbPlanes, activePoolIds) {
        activePoolIds.mapNotNull { id -> dbPlanes.find { it.id == id } }
    }

    LaunchedEffect(dbPlanes) {
        if (dbPlanes.isNotEmpty() && activePoolIds.isEmpty()) {
            activePoolIds = dbPlanes.shuffled().map { it.id }
        }
    }

    var isRolling by remember { mutableStateOf(false) }
    var rollResult by remember { mutableStateOf<DieSide?>(null) }
    val scope = rememberCoroutineScope()

    val rollDie = {
        scope.launch {
            isRolling = true
            delay(1000) // Simulate rolling
            val sides = listOf(
                DieSide.Planeswalker,
                DieSide.Chaos,
                DieSide.Blank,
                DieSide.Blank,
                DieSide.Blank,
                DieSide.Blank
            )
            val result = sides[Random.nextInt(sides.size)]
            rollResult = result
            isRolling = false

            // Auto-action and auto-dismiss after a short delay
            delay(1500)
            if (result == DieSide.Planeswalker) {
                val currentId = activePoolIds[currentIndex]
                val newPoolIds = activePoolIds.filter { it != currentId }

                if (newPoolIds.isEmpty()) {
                    activePoolIds = dbPlanes.shuffled().map { it.id }
                    currentIndex = 0
                } else {
                    activePoolIds = newPoolIds
                    currentIndex = Random.nextInt(newPoolIds.size)
                }
            }
            rollResult = null
        }
    }

    LaunchedEffect(Unit) {
        delay(10_000)
        timedOut = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when {
            activePool.isNotEmpty() -> {
                val plane = activePool[currentIndex]

                SubcomposeAsyncImage(
                    model = File(plane.image).takeIf { it.exists() },
                    contentDescription = plane.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 48.dp)
                        .graphicsLayer(
                            scaleX = 0.9f,
                            scaleY = 0.9f
                        )
                        .clip(RoundedCornerShape(48.dp)),
                    contentScale = ContentScale.Fit,
                    loading = {
                        Box(Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    },
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = { rollDie() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 70.dp),
                    enabled = !isRolling && rollResult == null
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Lanzar dado",
                        modifier = Modifier.size(100.dp),
                        tint = Color.White
                    )
                }

                if (isRolling) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                rollResult?.let { result ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(32.dp)
                                .background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            
                            when (result) {
                                DieSide.Planeswalker -> {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        "¡Caminante de Planos!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White
                                    )
                                    Text(
                                        "Viajando...",
                                        color = Color.White
                                    )
                                }
                                DieSide.Chaos -> {
                                    Icon(
                                        imageVector = Icons.Default.Cyclone,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp),
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        "¡Caos!",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White
                                    )
                                    Text(
                                        "Se desata el caos.",
                                        color = Color.White
                                    )
                                }
                                DieSide.Blank -> {
                                    Spacer(modifier = Modifier.height(80.dp))
                                    Text(
                                        "Nada ocurre",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            timedOut -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "No se pudieron cargar los planos",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Comprueba la conexión a internet",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator()
                    Text(text = "Descargando planos...")
                }
            }
        }
    }
}

enum class DieSide {
    Planeswalker, Chaos, Blank
}
