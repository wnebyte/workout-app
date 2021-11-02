package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.wnebyte.workoutapp.model.Set
import java.util.*

@Dao
interface SetDao : IDao<Set> {

    @Query("DELETE FROM `set`")
    fun deleteAll()

    @Query("SELECT * FROM `Set` WHERE id=(:id)")
    fun get(id: UUID): LiveData<Set?>

    @Query("SELECT * FROM `Set`")
    fun getAll(): LiveData<List<Set>>
}