package com.carpes.planeschase.ui.planes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun PlaneViewerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val planes by remember {
        PlaneschaseDatabase.getInstance(context).planeDao().getAllPlanes()
    }.collectAsState(initial = emptyList())

    var currentIndex by remember { mutableIntStateOf(0) }
    var timedOut by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10_000)
        timedOut = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            planes.isNotEmpty() -> {
                val plane = planes[currentIndex]

                SubcomposeAsyncImage(
                    model = File(plane.image).takeIf { it.exists() },
                    contentDescription = plane.name,
                    modifier = Modifier.fillMaxSize(),
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
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        currentIndex = if (currentIndex == 0) planes.lastIndex else currentIndex - 1
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Anterior",
                            modifier = Modifier.size(48.dp),
                        )
                    }

                    Text(text = "${currentIndex + 1} / ${planes.size}")

                    IconButton(onClick = {
                        currentIndex = (currentIndex + 1) % planes.size
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Siguiente",
                            modifier = Modifier.size(48.dp),
                        )
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
