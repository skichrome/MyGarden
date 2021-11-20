package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StandardApiResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "result") val result: String
)