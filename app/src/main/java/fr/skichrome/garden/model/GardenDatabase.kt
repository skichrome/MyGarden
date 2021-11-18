package fr.skichrome.garden.model

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
        entities = [
            Device::class,
            DeviceConfiguration::class
        ],
        version = CURRENT_DATABASE_VERSION,
        exportSchema = false
)
abstract class GardenDatabase : RoomDatabase()
{
    abstract fun deviceDao(): DeviceDao
    abstract fun deviceConfigurationDao(): DeviceConfigurationDao
}

private const val CURRENT_DATABASE_VERSION = 1

fun buildDatabase(app: Application): RoomDatabase = Room.databaseBuilder(app, GardenDatabase::class.java, "GardenDatabase.db")
        .addMigrationObjects()
        .build()

private fun <T : RoomDatabase> RoomDatabase.Builder<T>.addMigrationObjects(): RoomDatabase.Builder<T> =
        addMigrations()