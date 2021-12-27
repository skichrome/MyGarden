package fr.skichrome.garden.device

import androidx.lifecycle.LiveData
import fr.skichrome.garden.androidmanager.NetManager
import fr.skichrome.garden.model.api.ApiSource
import fr.skichrome.garden.model.local.Device
import fr.skichrome.garden.model.local.DeviceConfSource
import fr.skichrome.garden.model.local.DeviceConfiguration
import fr.skichrome.garden.model.local.DeviceSource
import fr.skichrome.garden.util.ApiException
import fr.skichrome.garden.util.AppResult
import fr.skichrome.garden.util.NetworkUnavailableException

interface DeviceRepository
{
    // --- Devices --- //

    fun observeAllDevices(): LiveData<AppResult<List<Device>>>
    suspend fun loadDevice(deviceId: Long): AppResult<Device>

    // --- Device Config --- //

    suspend fun loadDeviceConfiguration(deviceId: Long): AppResult<DeviceConfiguration>

    // --- Network --- //

    suspend fun createDeviceAndConfiguration(device: Device, deviceConfiguration: DeviceConfiguration): AppResult<Long>
    suspend fun updateDeviceAndConfiguration(device: Device, deviceConfiguration: DeviceConfiguration): AppResult<Long>
}

class DeviceRepositoryImpl(
    private val netManager: NetManager,
    private val deviceSource: DeviceSource,
    private val deviceConfSource: DeviceConfSource,
    private val apiSource: ApiSource,
) : DeviceRepository
{
    // --- Devices --- //

    override fun observeAllDevices(): LiveData<AppResult<List<Device>>> = deviceSource.observeDevices()
    override suspend fun loadDevice(deviceId: Long): AppResult<Device> = deviceSource.getDevice(deviceId)

    // --- Device Config --- //

    override suspend fun loadDeviceConfiguration(deviceId: Long): AppResult<DeviceConfiguration> = deviceConfSource.getDeviceConfFromDevice(deviceId)

    // --- Network --- //

    override suspend fun createDeviceAndConfiguration(device: Device, deviceConfiguration: DeviceConfiguration): AppResult<Long>
    {
        return if (netManager.isConnected)
        {
            val deviceCreationResult = apiSource.createNewDevice(
                uniqueId = device.deviceId,
                deviceName = device.name,
                description = device.description
            )

            if (deviceCreationResult is AppResult.Error)
                return deviceCreationResult

            if ((deviceCreationResult as AppResult.Success).data.result.isNullOrEmpty())
                return AppResult.Error(ApiException("Successfully created a new device but it's ID isn't returned by api (${deviceCreationResult.data})"))

            val deviceConfigCreationResult = apiSource.pushDeviceConfiguration(
                deviceRef = deviceCreationResult.data.result!![0].id,
                startTimeHour = deviceConfiguration.startTimeHour,
                startTimeMin = deviceConfiguration.startTimeMin,
                duration = deviceConfiguration.duration
            )

            if (deviceConfigCreationResult is AppResult.Error)
                return deviceConfigCreationResult

            AppResult.Success(deviceCreationResult.data.result[0].id)
        } else
            AppResult.Error(NetworkUnavailableException("Can't create device with configuration"))
    }

    override suspend fun updateDeviceAndConfiguration(device: Device, deviceConfiguration: DeviceConfiguration): AppResult<Long>
    {
        return if (netManager.isConnected)
        {
            val deviceUpdateResult = apiSource.updateDevice(
                id = device.id,
                uniqueId = device.deviceId,
                deviceName = device.name,
                description = device.description
            )

            if (deviceUpdateResult is AppResult.Error)
                return deviceUpdateResult

            val deviceConfigurationUpdateResult = apiSource.pushDeviceConfiguration(
                deviceRef = deviceConfiguration.id,
                startTimeHour = deviceConfiguration.startTimeHour,
                startTimeMin = deviceConfiguration.startTimeMin,
                duration = deviceConfiguration.duration
            )

            if (deviceConfigurationUpdateResult is AppResult.Error)
                return deviceConfigurationUpdateResult

            AppResult.Success(device.id)
        } else
            AppResult.Error(NetworkUnavailableException("Can't create device with configuration"))
    }
}