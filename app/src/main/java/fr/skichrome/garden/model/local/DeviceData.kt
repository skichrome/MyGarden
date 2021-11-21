package fr.skichrome.garden.model.local

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
    suspend fun getDeviceDataByDevice(deviceRef: Long): List<DeviceData>
}

interface DeviceDataSource
{
    suspend fun getDevicesDataByDevice(deviceRef: Long): AppResult<List<DeviceData>>
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

    override suspend fun getDevicesDataByDevice(deviceRef: Long): AppResult<List<DeviceData>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceDataDao.getDeviceDataByDevice(deviceRef)
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }

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