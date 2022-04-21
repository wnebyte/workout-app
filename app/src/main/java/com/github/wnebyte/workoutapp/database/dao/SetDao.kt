package com.github.wnebyte.workoutapp.database.dao

import java.util.*
import androidx.room.*
import android.database.Cursor
import androidx.lifecycle.LiveData
import com.github.wnebyte.workoutapp.model.Set

@Dao
interface SetDao : IDao<Set> {

    @Query("DELETE FROM `set`")
    fun deleteAll()

    @Query("SELECT * FROM `set` WHERE id=(:id)")
    fun get(id: UUID): LiveData<Set?>

    @Query("SELECT * FROM `set`")
    fun getAll(): LiveData<List<Set>>

    @Query("SELECT * FROM `set`")
    fun getAllRaw(): Cursor
}