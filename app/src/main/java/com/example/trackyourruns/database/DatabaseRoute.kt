package com.example.trackyourruns.database

import androidx.room.*

/*
* Database to store Routes
* */
@Database(entities = [Route::class], version = 1)
abstract class DatabaseRoute : RoomDatabase() {

    abstract fun daoRoute(): DaoRoute
}

@Entity
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val distance: Int,
)

@Dao
interface DaoRoute {

    @Insert
    suspend fun insertRoute(route: Route)

    @Update
    suspend fun updateRoute(route: Route)

    @Delete
    suspend fun deleteRoute(route: Route)

    @Query("DELETE FROM route")
    suspend fun deleteAll()

    @Query("SELECT * FROM route")
    suspend fun selectAllRoutes(): List<Route>

    @Query("SELECT * FROM route WHERE id=:id")
    suspend fun selectRoute(id: Int): Route

    @Query("SELECT name FROM route")
    suspend fun getAllRouteNames(): List<String>

    @Query("SELECT name FROM route where id=:id")
    suspend fun getRouteName(id: Int): String

    @Query("SELECT * FROM route WHERE name=:name")
    suspend fun selectRoute(name: String): Route

    @Query("SELECT COUNT(name) FROM route WHERE name=:name")
    suspend fun countRoutes(name: String): Int
}