package fr.skichrome.garden.model.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import fr.skichrome.garden.BuildConfig
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import timber.log.Timber

interface ApiService
{
    companion object
    {
        private const val API_KEY_HEADER = "apikey"
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private val loggerInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        private val headerInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            requestBuilder.header(API_KEY_HEADER, BuildConfig.API_KEY)
            return@Interceptor chain.proceed(requestBuilder.build())
        }

        fun getApiService(debugNeeded: Boolean = false): ApiService = Retrofit.Builder().apply {
            baseUrl(BuildConfig.API_BASE_URL)
            addConverterFactory(MoshiConverterFactory.create(moshi))
            if (debugNeeded)
                client(OkHttpClient.Builder().addInterceptor(loggerInterceptor).build())
            client(OkHttpClient.Builder().addInterceptor(headerInterceptor).build())
        }.build().create(ApiService::class.java)
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
    ): DeviceResponse

    @FormUrlEncoded
    @POST("devices/{deviceId}")
    suspend fun updateDevice(
        @Path("deviceId") id: Long,
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
    suspend fun createNewDevice(uniqueId: String, deviceName: String, description: String?): AppResult<DeviceResponse>
    suspend fun updateDevice(id: Long, uniqueId: String, deviceName: String, description: String?): AppResult<StandardApiResponse>
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
            Timber.e(e, "An error occurred when trying to get device configuration")
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
            Timber.e(e, "An error occurred when trying to push device configuration")
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
            Timber.e(e, "An error occurred when trying to get devices list")
            AppResult.Error(e)
        }
    }

    override suspend fun createNewDevice(uniqueId: String, deviceName: String, description: String?): AppResult<DeviceResponse> =
        withContext(dispatcher) {
            return@withContext try
            {
                val result = service.createNewDevice(uniqueId, deviceName, description)
                AppResult.Success(result)
            } catch (e: Throwable)
            {
                Timber.e(e, "An error occurred when trying to create new device")
                AppResult.Error(e)
            }
        }

    override suspend fun updateDevice(id: Long, uniqueId: String, deviceName: String, description: String?): AppResult<StandardApiResponse> =
        withContext(dispatcher) {
            return@withContext try
            {
                val result = service.updateDevice(id, uniqueId, deviceName, description)
                AppResult.Success(result)
            } catch (e: Throwable)
            {
                Timber.e(e, "An error occurred when trying to update device ($deviceName)")
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
            Timber.e(e, "An error occurred when trying to get sensors data")
            AppResult.Error(e)
        }
    }
}