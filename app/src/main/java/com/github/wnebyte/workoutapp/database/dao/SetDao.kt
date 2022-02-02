package com.github.wnebyte.workoutapp.database.dao

import java.util.*
import androidx.room.*
import androidx.lifecycle.LiveData
import com.github.wnebyte.workoutapp.model.Set

@Dao
interface SetDao : IDao<Set> {

    @Query("DELETE FROM `set`")
    fun deleteAll()

    @Query("SELECT * FROM `Set` WHERE id=(:id)")
    fun get(id: UUID): LiveData<Set?>

    @Query("SELECT * FROM `Set`")
    fun getAll(): LiveData<List<Set>>
}