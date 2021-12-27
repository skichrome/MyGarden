package fr.skichrome.garden.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(
    tableName = "devices_configuration",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeviceConfiguration(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "start_time_hour") val startTimeHour: Int,
    @ColumnInfo(name = "start_time_min") val startTimeMin: Int,
    @ColumnInfo(name = "duration") val duration: Int
)

@Dao
interface DeviceConfigurationDao : BaseDao<DeviceConfiguration>
{
    @Query("SELECT * FROM devices_configuration WHERE id == :deviceRef LIMIT 1")
    fun observeDeviceConfigurationFromDevice(deviceRef: Long): LiveData<DeviceConfiguration>

    @Query("SELECT * FROM devices_configuration WHERE id == :deviceRef LIMIT 1")
    suspend fun getDeviceConfigurationFromDevice(deviceRef: Long): DeviceConfiguration
}

interface DeviceConfSource
{
    fun observeDeviceConfigurationFromDevice(deviceRef: Long): LiveData<AppResult<DeviceConfiguration>>
    suspend fun getDeviceConfFromDevice(deviceRef: Long): AppResult<DeviceConfiguration>
    suspend fun insertDeviceConf(deviceConfiguration: DeviceConfiguration): AppResult<Long>
}

class DeviceConfSourceImpl(db: GardenDatabase, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : DeviceConfSource
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceConfigurationDao: DeviceConfigurationDao = db.deviceConfigurationDao()

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun observeDeviceConfigurationFromDevice(deviceRef: Long): LiveData<AppResult<DeviceConfiguration>> =
        deviceConfigurationDao.observeDeviceConfigurationFromDevice(deviceRef).map { AppResult.Success(it) }

    override suspend fun getDeviceConfFromDevice(deviceRef: Long): AppResult<DeviceConfiguration> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceConfigurationDao.getDeviceConfigurationFromDevice(deviceRef)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }

    override suspend fun insertDeviceConf(deviceConfiguration: DeviceConfiguration): AppResult<Long> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceConfigurationDao.insertReplace(deviceConfiguration)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }
}