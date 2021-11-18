package fr.skichrome.garden.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(
        tableName = "devices"
)
data class Device(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Long,
        @ColumnInfo(name = "device_id") val deviceId: String
)

@Dao
interface DeviceDao : BaseDao<Device>
{
    @Query("SELECT * FROM devices")
    fun observeDevices(): LiveData<List<Device>>
}

interface DeviceSource

class DeviceSourceImpl : DeviceSource
{
}