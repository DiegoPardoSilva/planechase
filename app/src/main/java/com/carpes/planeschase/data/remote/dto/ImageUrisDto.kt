package com.carpes.planeschase.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageUrisDto(
    @SerializedName("normal") val normal: String,
)
