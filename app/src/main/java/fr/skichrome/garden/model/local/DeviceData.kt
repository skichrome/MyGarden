package fr.skichrome.garden.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(
    tableName = "devices_data",
    foreignKeys = [
        ForeignKey(
            entity = Device::class,
            parentColumns = ["id"],
            childColumns = ["device_ref"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeviceData(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Long,
    @ColumnInfo(name = "device_ref", index = true) val deviceRef: Long,
    @ColumnInfo(name = "altitude") val altitude: Long,
    @ColumnInfo(name = "barometric") val barometric: Long,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "soil_moisture") val soilMoisture: Double,
    @ColumnInfo(name = "luminosity") val luminosity: Long,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Dao
interface DeviceDataDao : BaseDao<DeviceData>
{
    @Query("SELECT * FROM devices_data WHERE device_ref == :deviceRef")
    fun observeDeviceDataByDevice(deviceRef: Long): LiveData<List<DeviceData>>
}

interface DeviceDataSource
{
    suspend fun observeDevicesDataByDevice(deviceRef: Long): LiveData<AppResult<List<DeviceData>>>
    suspend fun insertDevicesData(devicesData: List<DeviceData>): AppResult<List<Long>>
}

class DeviceDataSourceImpl(db: GardenDatabase, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : DeviceDataSource
{
    // ===================================
    //               Fields
    // ===================================

    private val deviceDataDao: DeviceDataDao = db.deviceDataDao()

    // ===================================
    //         Superclass Methods
    // ===================================

    override suspend fun observeDevicesDataByDevice(deviceRef: Long): LiveData<AppResult<List<DeviceData>>> =
        deviceDataDao.observeDeviceDataByDevice(deviceRef).map { AppResult.Success(it) }

    override suspend fun insertDevicesData(devicesData: List<DeviceData>): AppResult<List<Long>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceDataDao.insertIgnore(*devicesData.toTypedArray())
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }
}