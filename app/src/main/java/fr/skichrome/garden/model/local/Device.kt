package fr.skichrome.garden.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(
    tableName = "devices"
)
data class Device(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "device_id") val deviceId: String,
    @ColumnInfo(name = "device_name") val name: String,
    @ColumnInfo(name = "description") val description: String?
)

@Dao
interface DeviceDao : BaseDao<Device>
{
    @Query("SELECT * FROM devices")
    fun observeDevices(): LiveData<List<Device>>

    @Query("SELECT * FROM devices")
    suspend fun getAllDeviceId(): List<Device>
}

interface DeviceSource
{
    fun observeDevices(): LiveData<AppResult<List<Device>>>
    suspend fun insertDevices(devices: List<Device>): AppResult<List<Long>>
    suspend fun getAllDevicesId(): AppResult<List<Long>>
}

class DeviceSourceImpl(db: GardenDatabase, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : DeviceSource
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceDao: DeviceDao = db.deviceDao()

    // ===================================
    //         Superclass Methods
    // ===================================

    override fun observeDevices(): LiveData<AppResult<List<Device>>> =
        deviceDao.observeDevices().map { AppResult.Success(it) }

    override suspend fun insertDevices(devices: List<Device>): AppResult<List<Long>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceDao.insertIgnore(*devices.toTypedArray())
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }

    override suspend fun getAllDevicesId(): AppResult<List<Long>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceDao.getAllDeviceId().map { it.id }
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }
}