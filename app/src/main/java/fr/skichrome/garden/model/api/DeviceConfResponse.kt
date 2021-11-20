package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class DeviceConfResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "status") val status: String,
    @Json(name = "result") val result: DeviceConfResult?
)

data class DeviceConfResult(
    @Json(name = "time_start_hour") val timeStartHour: Int,
    @Json(name = "time_start_min") val timeStartMin: Int,
    @Json(name = "duration") val duration: Int
)