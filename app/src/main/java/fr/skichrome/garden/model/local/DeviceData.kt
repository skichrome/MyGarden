package fr.skichrome.garden.model.local

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import fr.skichrome.garden.model.DeviceFilter
import fr.skichrome.garden.util.AppResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
    @ColumnInfo(name = "altitude") val altitude: Double,
    @ColumnInfo(name = "barometric") val barometric: Double,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "soil_moisture") val soilMoisture: Long,
    @ColumnInfo(name = "luminosity") val luminosity: Long,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Dao
interface DeviceDataDao : BaseDao<DeviceData>
{
    @Query("SELECT * FROM devices_data WHERE device_ref == :deviceRef")
    suspend fun getDeviceDataByDevice(deviceRef: Long): List<DeviceData>

    @RawQuery(observedEntities = [DeviceData::class])
    suspend fun getDeviceDataFromRawQuery(sqlQuery: SupportSQLiteQuery): List<DeviceData>
}

interface DeviceDataSource
{
    suspend fun getDevicesDataByDevice(deviceRef: Long, filter: DeviceFilter): AppResult<List<DeviceData>>
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

    override suspend fun getDevicesDataByDevice(deviceRef: Long, filter: DeviceFilter): AppResult<List<DeviceData>> = withContext(dispatcher) {
        return@withContext try
        {
            val notFilteredQuery = "SELECT * FROM devices_data WHERE device_ref == $deviceRef"
            val rawQueryBuilder = StringBuilder(notFilteredQuery)
            filter.startDate?.let { startDate -> rawQueryBuilder.append(" AND timestamp >= $startDate") }
            filter.endDate?.let { endDate -> rawQueryBuilder.append(" AND timestamp <= $endDate") }

            rawQueryBuilder.append(" ORDER BY timestamp")

            val fullQuery = rawQueryBuilder.toString()

            Timber.w("Query: $fullQuery")

            if (fullQuery == notFilteredQuery)
                return@withContext AppResult.Success(deviceDataDao.getDeviceDataByDevice(deviceRef))

            val result = deviceDataDao.getDeviceDataFromRawQuery(SimpleSQLiteQuery(fullQuery))
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }

    override suspend fun insertDevicesData(devicesData: List<DeviceData>): AppResult<List<Long>> = withContext(dispatcher) {
        return@withContext try
        {
            val result = deviceDataDao.insertReplace(*devicesData.toTypedArray())
            AppResult.Success(result)
        } catch (e: Throwable)
        {
            AppResult.Error(e)
        }
    }
}