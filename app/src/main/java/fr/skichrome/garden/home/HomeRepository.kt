package fr.skichrome.garden.home

import androidx.lifecycle.LiveData
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceData
import fr.skichrome.garden.model.local.DeviceDataSource
import fr.skichrome.garden.model.local.DeviceSource
import fr.skichrome.garden.util.AppResult

interface HomeRepository
{
    // --- Devices --- //

    fun observeAllDevices(): LiveData<AppResult<List<Device>>>
    suspend fun loadDevice(deviceId: Long): AppResult<Device>

    // --- Device Data --- //

    suspend fun getDeviceData(deviceId: Long): AppResult<List<DeviceData>>
}

class HomeRepositoryImpl(private val deviceSource: DeviceSource, private val deviceDataSource: DeviceDataSource) : HomeRepository
{
    // --- Devices --- //

    override fun observeAllDevices(): LiveData<AppResult<List<Device>>> = deviceSource.observeDevices()
    override suspend fun loadDevice(deviceId: Long): AppResult<Device> = deviceSource.getDevice(deviceId)

    // --- Device Data --- //

    override suspend fun getDeviceData(deviceId: Long): AppResult<List<DeviceData>> = deviceDataSource.getDevicesDataByDevice(deviceId)
}