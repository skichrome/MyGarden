package fr.skichrome.garden.service

import fr.skichrome.garden.androidmanager.NetManager
import fr.skichrome.garden.model.api.ApiSource
import fr.skichrome.garden.model.local.*
import fr.skichrome.garden.util.ApiException
import fr.skichrome.garden.util.AppResult
import fr.skichrome.garden.util.NetworkUnavailableException
import timber.log.Timber

interface DataSyncRepository
{
    suspend fun synchronizeDevices(): AppResult<List<Long>>
    suspend fun synchronizeDeviceConfiguration(deviceId: Long): AppResult<Boolean>
    suspend fun synchronizeDeviceData(deviceId: Long): AppResult<Boolean>
}

class DataSyncRepositoryImpl(
    private val netManager: NetManager,
    private val apiSource: ApiSource,
    private val deviceSource: DeviceSource,
    private val deviceConfSource: DeviceConfSource,
    private val deviceDataSource: DeviceDataSource
) : DataSyncRepository
{
    override suspend fun synchronizeDevices(): AppResult<List<Long>>
    {
        return if (netManager.isConnected)
        {
            // 1. Get devices from API
            val apiResult = apiSource.getDevices()
            if (apiResult is AppResult.Error)
                return apiResult

            if ((apiResult as AppResult.Success).data.statusCode != 200)
                return AppResult.Error(ApiException(apiResult.data.status))

            // 2. Delete difference from devices in local and remote
            val previousDevicesIds = deviceSource.getAllDevicesId()

            if (previousDevicesIds is AppResult.Error)
                return previousDevicesIds

            val apiIds = apiResult.data.result?.map { it.id } ?: emptyList()
            (previousDevicesIds as AppResult.Success).data.map { localId ->
                if (!apiIds.contains(localId))
                {
                    Timber.w("Device with ID [$localId] isn't available on remote, delete it")
                    deviceSource.deleteDevice(localId)
                }
            }

            // 3. Insert devices in local database
            val localDevices = apiResult.data.result?.map {
                Device(it.id, it.deviceId, it.name, it.description)
            } ?: emptyList()

            val localResult = deviceSource.insertDevices(localDevices)
            if (localResult is AppResult.Error)
                return localResult

            // 4. Get all ids from database, useful for next sync calls
            val deviceIdsResult = deviceSource.getAllDevicesId()
            if (deviceIdsResult is AppResult.Error)
                return deviceIdsResult

            AppResult.Success((deviceIdsResult as AppResult.Success).data)
        } else
            AppResult.Error(NetworkUnavailableException("Network error"))

    }

    override suspend fun synchronizeDeviceConfiguration(deviceId: Long): AppResult<Boolean>
    {
        return if (netManager.isConnected)
        {
            // 1. Load device conf from device
            val apiResult = apiSource.getDeviceConfiguration(deviceId)
            if (apiResult is AppResult.Error)
                return apiResult

            if ((apiResult as AppResult.Success).data.statusCode != 200)
                return AppResult.Error(ApiException(apiResult.data.status))

            // 2. Insert device conf in local database
            val localDeviceConf = apiResult.data.result?.let {
                DeviceConfiguration(
                    id = deviceId,
                    startTimeHour = it.timeStartHour,
                    startTimeMin = it.timeStartMin,
                    duration = it.duration
                )
            }

            localDeviceConf?.let {
                val localResult = deviceConfSource.insertDeviceConf(it)
                if (localResult is AppResult.Error)
                    return localResult
            }

            AppResult.Success(true)
        } else
            AppResult.Error(NetworkUnavailableException("Network error"))
    }

    override suspend fun synchronizeDeviceData(deviceId: Long): AppResult<Boolean>
    {
        return if (netManager.isConnected)
        {
            // 1. Load device conf from device
            val apiResult = apiSource.getSensorsDataFromDevice(deviceId)
            if (apiResult is AppResult.Error)
                return apiResult

            if ((apiResult as AppResult.Success).data.statusCode != 200)
                return AppResult.Error(ApiException(apiResult.data.status))

            // 2. Insert device conf in local database
            val localDeviceData = apiResult.data.result?.map {
                DeviceData(
                    id = it.id,
                    deviceRef = deviceId,
                    altitude = it.altitude,
                    barometric = it.barometric,
                    temperature = it.temperature,
                    soilMoisture = it.soilMoisture,
                    luminosity = it.luminosity,
                    timestamp = it.timestamp
                )
            } ?: emptyList()

            val localResult = deviceDataSource.insertDevicesData(localDeviceData)
            if (localResult is AppResult.Error)
                return localResult

            AppResult.Success(true)
        } else
            AppResult.Error(NetworkUnavailableException("Network error"))
    }
}