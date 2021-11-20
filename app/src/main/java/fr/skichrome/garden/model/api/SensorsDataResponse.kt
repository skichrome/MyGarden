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
    @Json(name = "altitude") val altitude: Long,
    @Json(name = "barometric") val barometric: Long,
    @Json(name = "temperature") val temperature: Double,
    @Json(name = "soil_moisture") val soilMoisture: Double,
    @Json(name = "luminosity") val luminosity: Long,
    @Json(name = "timestamp") val timestamp: Long
)