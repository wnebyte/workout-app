package com.github.wnebyte.workoutapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.wnebyte.workoutapp.model.Session
import java.util.*

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(session: Session)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(vararg  session: Session)

    @Delete
    fun delete(session: Session)

    @Delete
    fun delete(vararg session: Session)

    @Query("DELETE FROM session")
    fun deleteAll()

    @Query("SELECT * FROM session WHERE id=(:id)")
    fun get(id: UUID): LiveData<Session?>

    @Query("SELECT * FROM session")
    fun getAll(): LiveData<List<Session>>

    @Query("SELECT * FROM session ORDER BY date ASC")
    fun getOrdered(): LiveData<List<Session>>
}