package com.example.trackyourruns.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset

/*
* TypeConverters for Database Run
* */
object RunConverters {

    /*
    * Converts LocalDateTime to Long to store in Database
    * */
    @TypeConverter
    fun fromLocalDateTime(time: LocalDateTime): Long {
        return time.toEpochSecond(ZoneOffset.UTC)
    }

    /*
    * Converts Long to LocalDateTime to work with the Date
    * */
    @TypeConverter
    fun toLocalDateTime(time: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(time, 0, ZoneOffset.UTC)
    }


    /*
    * Converts a List with Double values (km-Times) to String to store in Database
    * */
    @TypeConverter
    fun fromListDouble(times: List<Double?>?): String? {
        if (times == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Double?>?>() {}.type
        return gson.toJson(times, type)
    }

    /*
    * Converts a String to a List with Double Values to work with the km-Times
    * */
    @TypeConverter
    fun toListDouble(times: String?): List<Double?>? {
        if (times == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Double?>?>() {}.type
        return gson.fromJson(times, type)
    }
}