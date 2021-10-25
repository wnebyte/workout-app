package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.wnebyte.workoutapp.model.Set
import java.util.*

@Dao
interface SetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(set: Set)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg set: Set)

    @Delete
    fun delete(set: Set)

    @Delete
    fun delete(vararg set: Set)

    @Update
    fun update(set: Set)

    @Update
    fun update(vararg set: Set)

    @Query("DELETE FROM `set`")
    fun deleteAll()

    @Query("SELECT * FROM `Set` WHERE id=(:id)")
    fun get(id: UUID): LiveData<Set?>

    @Query("SELECT * FROM `Set`")
    fun getAll(): LiveData<List<Set>>
}