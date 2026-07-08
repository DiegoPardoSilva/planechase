package com.carpes.planeschase.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScryfallCardDto(
    @SerializedName("name") val name: String,
    @SerializedName("type_line") val typeLine: String,
    @SerializedName("oracle_text") val oracleText: String?,
    @SerializedName("image_uris") val imageUris: ImageUrisDto?,
)
