package com.carpes.planeschase.data.seeder

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import com.carpes.planeschase.data.local.entity.PlaneEntity
import com.carpes.planeschase.data.local.entity.PlaneSetEntity
import com.carpes.planeschase.data.remote.dto.ScryfallListDto
import com.google.gson.Gson
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "LocalSeeder"
private const val CHAOS_MARKER = "Whenever chaos ensues"

private data class SetAsset(
    val assetFile: String,
    val name: String,
    val code: String,
    val releaseYear: Int,
    val image: String,
)

private val SETS = listOf(
    SetAsset(
        assetFile = "planes_opca.json",
        name = "Planechase Anthology",
        code = "opca",
        releaseYear = 2016,
        image = "https://svgs.scryfall.io/sets/opca.svg",
    ),
)

class LocalSeeder(
    private val context: Context,
    private val db: PlaneschaseDatabase,
    private val assets: AssetManager,
) {
    private val gson = Gson()

    suspend fun seedIfEmpty() {
        if (db.planeDao().count() > 0) {
            Log.d(TAG, "DB ya tiene datos, saltando seed")
            return
        }

        val imagesDir = File(context.filesDir, "planes").also { it.mkdirs() }

        for (set in SETS) {
            try {
                val json = assets.open(set.assetFile).bufferedReader().readText()
                val listDto = gson.fromJson(json, ScryfallListDto::class.java)

                val setIds = db.planeSetDao().insertAll(
                    listOf(
                        PlaneSetEntity(
                            name = set.name,
                            code = set.code,
                            releaseYear = set.releaseYear,
                            image = set.image,
                        )
                    )
                )
                val setId = setIds.first().toInt()
                Log.d(TAG, "Descargando imágenes de '${set.code}'...")

                val planes = listDto.data.mapIndexed { index, card ->
                    val oracleText = card.oracleText.orEmpty()
                    val chaosIndex = oracleText.indexOf(CHAOS_MARKER)
                    val description: String
                    val chaosAbility: String?
                    if (chaosIndex >= 0) {
                        description = oracleText.substring(0, chaosIndex).trim()
                        chaosAbility = oracleText.substring(chaosIndex)
                    } else {
                        description = oracleText
                        chaosAbility = null
                    }

                    val localImagePath = downloadImage(
                        url = card.imageUris?.normal.orEmpty(),
                        dir = imagesDir,
                        filename = "${set.code}_$index",
                    )

                    Log.d(TAG, "${index + 1}/${listDto.data.size} ${card.name}")

                    PlaneEntity(
                        name = card.name,
                        image = localImagePath,
                        description = description,
                        chaosAbility = chaosAbility,
                        typeLine = card.typeLine,
                        setId = setId,
                    )
                }

                db.planeDao().insertAll(planes)
                Log.d(TAG, "✓ ${planes.size} planos insertados para '${set.code}'")

            } catch (e: Exception) {
                Log.e(TAG, "Error cargando '${set.assetFile}'", e)
            }
        }
    }

    private fun downloadImage(url: String, dir: File, filename: String): String {
        if (url.isEmpty()) return ""
        val file = File(dir, "$filename.jpg")
        if (file.exists()) return file.absolutePath
        return try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 15_000
            connection.readTimeout = 30_000
            connection.connect()
            file.outputStream().use { out ->
                connection.inputStream.use { it.copyTo(out) }
            }
            connection.disconnect()
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error descargando imagen: $url", e)
            ""
        }
    }
}
