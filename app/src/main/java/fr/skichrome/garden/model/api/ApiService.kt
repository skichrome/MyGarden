package fr.skichrome.garden.model.api

import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import timber.log.Timber

interface ApiService
{
    companion object
    {
        fun getApiService(): ApiService = Retrofit.Builder()
            .baseUrl("https://nodered.campeoltoni.fr/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @GET("device/{deviceId}/configuration")
    suspend fun getDeviceConfiguration(@Path("deviceId") deviceId: Long): DeviceConfResponse

    @FormUrlEncoded
    @POST("device/{deviceId}/configuration")
    suspend fun pushDeviceConfiguration(
        @Path("deviceId") deviceRef: Long,
        @Field("start_time_hour") startTimeHour: Int,
        @Field("start_time_min") startTimeMin: Int,
        @Field("duration") duration: Int
    ): StandardApiResponse

    @GET("devices")
    suspend fun getDevices(): DeviceResponse

    @FormUrlEncoded
    @POST("devices")
    suspend fun createNewDevice(
        @Field("device_unique_id") uniqueId: String,
        @Field("device_name") deviceName: String,
        @Field("device_description") description: String?
    ): StandardApiResponse

    @GET("device/{deviceId}/sensors-data")
    suspend fun getSensorsData(@Path("deviceId") deviceRef: Long): SensorsDataResponse
}

interface ApiSource
{
    suspend fun getDeviceConfiguration(deviceId: Long): AppResult<DeviceConfResponse>
    suspend fun pushDeviceConfiguration(deviceRef: Long, startTimeHour: Int, startTimeMin: Int, duration: Int): AppResult<StandardApiResponse>
    suspend fun getDevices(): AppResult<DeviceResponse>
    suspend fun createNewDevice(uniqueId: String, deviceName: String, description: String?): AppResult<StandardApiResponse>
    suspend fun getSensorsDataFromDevice(deviceRef: Long): AppResult<SensorsDataResponse>
}

class ApiSourceImpl(private val service: ApiService, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : ApiSource
{
    override suspend fun getDeviceConfiguration(deviceId: Long): AppResult<DeviceConfResponse> = withContext(dispatcher) {
        return@withContext try
        {
            val result = service.getDeviceConfiguration(deviceId)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            Timber.e("An error occurred when trying to get device configuration")
            AppResult.Error(e)
        }
    }

    override suspend fun pushDeviceConfiguration(
        deviceRef: Long, startTimeHour: Int, startTimeMin: Int, duration: Int
    ): AppResult<StandardApiResponse> = withContext(dispatcher) {
        return@withContext try
        {
            val result = service.pushDeviceConfiguration(deviceRef, startTimeHour, startTimeMin, duration)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            Timber.e("An error occurred when trying to push device configuration")
            AppResult.Error(e)
        }
    }

    override suspend fun getDevices(): AppResult<DeviceResponse> = withContext(dispatcher) {
        return@withContext try
        {
            val result = service.getDevices()
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            Timber.e("An error occurred when trying to get devices list")
            AppResult.Error(e)
        }
    }

    override suspend fun createNewDevice(uniqueId: String, deviceName: String, description: String?): AppResult<StandardApiResponse> =
        withContext(dispatcher) {
            return@withContext try
            {
                val result = service.createNewDevice(uniqueId, deviceName, description)
                AppResult.Success(result)
            } catch (e: Throwable)
            {
                Timber.e("An error occurred when trying to create new device")
                AppResult.Error(e)
            }
        }

    override suspend fun getSensorsDataFromDevice(deviceRef: Long): AppResult<SensorsDataResponse> = withContext(dispatcher) {
        return@withContext try
        {
            val result = service.getSensorsData(deviceRef)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            Timber.e("An error occurred when trying to get sensors data")
            AppResult.Error(e)
        }
    }
}