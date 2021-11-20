package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeviceResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "status") val status: String,
    @Json(name = "result") val result: List<DeviceResult>?
)

@JsonClass(generateAdapter = true)
data class DeviceResult(
    @Json(name = "id") val id: Long,
    @Json(name = "device_id") val deviceId: String,
    @Json(name = "name") val name: String,
    @Json(name = "Description") val description: String?
)