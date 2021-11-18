package fr.skichrome.garden.model

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T>
{
    @Insert
    fun insert(entity: T): Long

    @Insert
    fun insert(vararg entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(vararg entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(vararg entity: T): Long

    @Update
    fun update(entity: T): Int

    @Update
    fun update(vararg entity: T): Int

    @Delete
    fun delete(entity: T): Int

    @Delete
    fun delete(vararg entity: T): Int
}