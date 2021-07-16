package com.example.trackyourruns.database

import androidx.room.*
import java.time.LocalDateTime

/*
* Database to Store Runs
* */
@Database(entities = [Run::class], version = 1)// [Run::class] = arrayOf(Run::class)
@TypeConverters(RunConverters::class)
abstract class DatabaseRun : RoomDatabase() {

    abstract fun daoRun(): DaoRun
}

@Entity
data class Run(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val startTime: LocalDateTime,
    val routeID: Int,
    val times: List<Double>,
    val intensity: Int
)

@Dao
interface DaoRun {

    @Insert
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("DELETE FROM run")
    suspend fun deleteAllRuns()

    @Query("SELECT * FROM run WHERE id=:id")
    suspend fun selectRun(id: Int): Run

    @Query("SELECT * FROM run")
    suspend fun selectAllRuns(): List<Run>

    @Query("SELECT * FROM run WHERE routeID=:routeID")
    suspend fun selectAllRunsByRoute(routeID: Int): List<Run>

    @Query("SELECT COUNT(id) FROM run WHERE id=:id")
    suspend fun countRouteUse(id: Int): Int
}