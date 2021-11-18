package fr.skichrome.garden.model

import androidx.room.*

@Entity(
        tableName = "devices_configuration",
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
data class DeviceConfiguration(
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long = 0L,
        @ColumnInfo(name = "device_ref", index = true) val deviceRef: Long,
        @ColumnInfo(name = "start_time_hour") val startTimeHour: Int,
        @ColumnInfo(name = "start_time_min") val startTimeMin: Int,
        @ColumnInfo(name = "duration") val duration: Int
)

@Dao
interface DeviceConfigurationDao : BaseDao<DeviceConfiguration>
{
    @Query("SELECT * FROM devices_configuration WHERE device_ref == :deviceRef LIMIT 1")
    fun getDeviceConfigurationFromDevice(deviceRef: Long): DeviceConfiguration
}

interface DeviceConfSource

class DeviceConfSourceImpl : DeviceConfSource
{
}