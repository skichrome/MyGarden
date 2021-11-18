package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class SensorsDataResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "result") val result: List<SensorsDataResult>
)

data class SensorsDataResult(
    @Json(name = "id") val id: Long,
    @Json(name = "altitude") val altitude: Long,
    @Json(name = "barometric") val barometric: Long,
    @Json(name = "temperature") val temperature: Long,
    @Json(name = "soil_moisture") val soilMoisture: Long,
    @Json(name = "luminosity") val luminosity: Long,
    @Json(name = "timestamp") val timestamp: Long
)