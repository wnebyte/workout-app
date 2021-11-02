package com.github.wnebyte.workoutapp.database.dao

import androidx.room.*

@Dao
interface IDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(t: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg t: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(list: List<T>)

    @Insert
    fun saveAndGetCount(t: T): Long

    @Delete
    fun delete(t: T)

    @Delete
    fun delete(vararg t: T)

    @Delete
    fun delete(list: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(t: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg t: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(list: List<T>)
}