package fr.skichrome.garden.model.local

import android.app.Application
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Device::class,
        DeviceConfiguration::class,
        DeviceData::class
    ],
    version = CURRENT_DATABASE_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class GardenDatabase : RoomDatabase()
{
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
    abstract fun deviceDataDao(): DeviceDataDao
}

private const val CURRENT_DATABASE_VERSION = 2
private const val CURRENT_DATABASE_NAME = "GardenDatabase.db"

fun buildDatabase(app: Application): GardenDatabase = Room.databaseBuilder(app, GardenDatabase::class.java, CURRENT_DATABASE_NAME)
    .addMigrationObjects()
    .build()

private fun <T : RoomDatabase> RoomDatabase.Builder<T>.addMigrationObjects(): RoomDatabase.Builder<T> =
    addMigrations()