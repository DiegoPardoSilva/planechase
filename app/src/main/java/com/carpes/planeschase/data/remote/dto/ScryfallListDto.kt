package com.carpes.planeschase.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScryfallListDto(
    @SerializedName("data") val data: List<ScryfallCardDto>,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("next_page") val nextPage: String?,
)
