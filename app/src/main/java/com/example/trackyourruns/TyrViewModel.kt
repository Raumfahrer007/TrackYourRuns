package com.example.trackyourruns

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.example.trackyourruns.database.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

/*
* ViewModel with instances of Databases and methods to access them
* */
class TyrViewModel : ViewModel() {

    lateinit var dbRun: DatabaseRun
    lateinit var daoRun: DaoRun
    lateinit var dbRoute: DatabaseRoute
    lateinit var daoRoute: DaoRoute
    lateinit var resources: Resources

    /*
    * static methods for converting
    * */
    companion object {
        fun dateToStringDisplay(date: Date): String {
            return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
        }

        fun dateToStringFormat(date: Date): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        }

        fun dateTimeToStringDisplay(dateTime: LocalDateTime): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            return formatter.format(dateTime)
        }

        fun dateTimeToStringDate(dateTime: LocalDateTime): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            return formatter.format(dateTime)
        }

        fun dateTimeToStringTime(dateTime: LocalDateTime): String {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            return formatter.format(dateTime)
        }

        fun dateTimeToDate(dateTime: LocalDateTime): LocalDate {
            return dateTime.toLocalDate()
        }

        fun dateTimeToTime(dateTime: LocalDateTime): LocalTime {
            return dateTime.toLocalTime()
        }
    }

    /*
    * Methods for accessing Database Route
    * */
    fun insertRoute(route: Route) = runBlocking {
        val insert = async {
            daoRoute.insertRoute(route)
        }
        insert.start()
        insert.await()
    }

    fun updateRoute(route: Route) = runBlocking {
        val update = async {
            daoRoute.updateRoute(route)
        }
        update.start()
        update.await()
    }

    fun deleteRoute(route: Route) = runBlocking {
        val delete = async {
            daoRoute.deleteRoute(route)
        }
        delete.start()
        delete.await()
    }

    fun selectAllRoutes() = runBlocking {
        val select = async {
            daoRoute.selectAllRoutes()
        }
        select.start()
        select.await()
    }

    fun selectAllRouteNames() = runBlocking {
        val select = async {
            daoRoute.getAllRouteNames()
        }
        select.start()
        select.await()
    }

    fun selectRoute(id: Int) = runBlocking {
        val select = async {
            daoRoute.selectRoute(id)
        }
        select.start()
        select.await()
    }

    fun selectRoute(name: String) = runBlocking {
        val select = async {
            daoRoute.selectRoute(name)
        }
        select.start()
        select.await()
    }

    fun countRoutes(name: String): Int = runBlocking {
        val count = async {
            daoRoute.countRoutes(name)
        }
        count.start()
        count.await()
    }

    /*
    * Methods for accessing Database Run
    * */
    fun insertRun(run: Run) = runBlocking {
        val insert = async {
            daoRun.insertRun(run)
        }
        insert.start()
        insert.await()
    }

    fun deleteRun(run: Run) = runBlocking {
        val delete = async {
            daoRun.deleteRun(run)
        }
        delete.start()
        delete.await()
    }

    fun deleteAllRuns() = runBlocking {
        val delete = async {
            daoRun.deleteAllRuns()
        }
        delete.start()
        delete.await()
    }

    fun selectRun(id: Int): Run = runBlocking {
        val select = async {
            daoRun.selectRun(id)
        }
        select.start()
        select.await()
    }

    fun selectAllRuns(): List<Run> = runBlocking {
        val select = async {
            daoRun.selectAllRuns()
        }
        select.start()
        select.await()
    }

    fun selectAllRunsByRoute(routeID: Int): List<Run> = runBlocking {
        val select = async {
            daoRun.selectAllRunsByRoute(routeID)
        }
        select.start()
        select.await()
    }

    fun countRouteUse(id: Int): Int = runBlocking {
        val count = async {
            daoRun.countRouteUse(id)
        }
        count.start()
        count.await()
    }
}