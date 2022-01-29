package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SensorsDataResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "status") val status: String,
    @Json(name = "result") val result: List<SensorsDataResult>?
)

@JsonClass(generateAdapter = true)
data class SensorsDataResult(
    @Json(name = "id") val id: Long,
    @Json(name = "altitude") val altitude: Double,
    @Json(name = "barometric") val barometric: Double,
    @Json(name = "temperature") val temperature: Double,
    @Json(name = "soil_moisture") val soilMoisture: Long,
    @Json(name = "luminosity") val luminosity: Long,
    @Json(name = "timestamp") val timestamp: Long
)