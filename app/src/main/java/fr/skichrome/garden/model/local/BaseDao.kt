package fr.skichrome.garden.model.local

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T>
{
    @Insert
    suspend fun insert(entity: T): Long

    @Insert
    suspend fun insert(vararg entity: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(vararg entity: T): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplace(vararg entity: T): List<Long>

    @Update
    suspend fun update(entity: T): Int

    @Update
    suspend fun update(vararg entity: T): Int

    @Delete
    suspend fun delete(entity: T): Int

    @Delete
    suspend fun delete(vararg entity: T): Int
}