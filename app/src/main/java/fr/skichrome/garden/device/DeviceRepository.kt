package fr.skichrome.garden.device

import androidx.lifecycle.LiveData
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceConfSource
import fr.skichrome.garden.model.local.DeviceConfiguration
import fr.skichrome.garden.model.local.DeviceSource
import fr.skichrome.garden.util.AppResult

interface DeviceRepository
{
    // --- Devices --- //

    fun observeAllDevices(): LiveData<AppResult<List<Device>>>
    suspend fun loadDevice(deviceId: Long): AppResult<Device>

    // --- Device Config --- //

    suspend fun loadDeviceConfiguration(deviceId: Long): AppResult<DeviceConfiguration>
}

class DeviceRepositoryImpl(
    private val deviceSource: DeviceSource,
    private val deviceConfSource: DeviceConfSource
) : DeviceRepository
{
    // --- Devices --- //

    override fun observeAllDevices(): LiveData<AppResult<List<Device>>> = deviceSource.observeDevices()
    override suspend fun loadDevice(deviceId: Long): AppResult<Device> = deviceSource.getDevice(deviceId)

    // --- Device Config --- //

    override suspend fun loadDeviceConfiguration(deviceId: Long): AppResult<DeviceConfiguration> = deviceConfSource.getDeviceConfFromDevice(deviceId)
}