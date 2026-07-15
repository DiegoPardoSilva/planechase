package com.carpes.planeschase.ui.planes

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import com.carpes.planeschase.data.local.entity.PlaneEntity
import com.carpes.planeschase.data.local.entity.PlaneSetEntity
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AddPlaneDialog(
    onDismiss: () -> Unit,
    onPlaneAdded: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { PlaneschaseDatabase.getInstance(context) }

    var name by remember { mutableStateOf("") }
    var typeLine by remember { mutableStateOf("Plane") }
    var description by remember { mutableStateOf("") }
    var chaosAbility by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Añadir nuevo Plano",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { launcher.launch("image/*") })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                            Text("Añadir foto", color = Color.White)
                        }
                    }
                }

                CustomTextField(value = name, onValueChange = { name = it }, label = "Nombre")
                CustomTextField(value = typeLine, onValueChange = { typeLine = it }, label = "Tipo (Plane, Phenomenon...)")
                CustomTextField(value = description, onValueChange = { description = it }, label = "Descripción", singleLine = false)
                CustomTextField(value = chaosAbility, onValueChange = { chaosAbility = it }, label = "Habilidad de Caos (opcional)", singleLine = false)

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && imageUri != null) {
                                scope.launch {
                                    val imagePath = saveUriToFile(context, imageUri!!)
                                    if (imagePath != null) {
                                        val setsCount = db.planeSetDao().count()
                                        val setId = if (setsCount == 0) {
                                            db.planeSetDao().insertAll(listOf(
                                                PlaneSetEntity(
                                                    name = "Custom",
                                                    code = "custom",
                                                    releaseYear = 2024,
                                                    image = ""
                                                )
                                            )).first().toInt()
                                        } else {
                                            1
                                        }

                                        val newId = db.planeDao().insert(
                                            PlaneEntity(
                                                name = name,
                                                image = imagePath,
                                                description = description,
                                                chaosAbility = chaosAbility.takeIf { it.isNotBlank() },
                                                typeLine = typeLine,
                                                setId = setId
                                            )
                                        )
                                        onPlaneAdded(newId.toInt())
                                    }
                                }
                            }
                        },
                        enabled = name.isNotBlank() && imageUri != null,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray
        )
    )
}

fun saveUriToFile(context: Context, uri: Uri): String? {
    val imagesDir = File(context.filesDir, "planes").also { it.mkdirs() }
    val filename = "custom_${System.currentTimeMillis()}.jpg"
    val file = File(imagesDir, filename)
    return try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        null
    }
}
