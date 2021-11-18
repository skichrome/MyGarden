package fr.skichrome.garden.model.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class DeviceConfResponse(
    @Json(name = "statusCode") val statusCode: Int,
    @Json(name = "result") val result: List<DeviceConfResult>
)

data class DeviceConfResult(
    @Json(name = "time_start_hour") val timeStartHour: Long,
    @Json(name = "time_start_min") val timeStartMin: Long,
    @Json(name = "duration") val duration: Long
)