package hu.bme.aut.szoftverarch.questly.data.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hu.bme.aut.szoftverarch.questly.data.tasks.Task

class Converters {
    @TypeConverter
    fun fromTask(task: Task): String {
        return Gson().toJson(task)
    }

    @TypeConverter
    fun toTask(taskString: String): Task {
        val taskType = object : TypeToken<Task>() {}.type
        return Gson().fromJson(taskString, taskType)
    }

    @TypeConverter
    fun fromLatlong(latLong: LatLong) : String {
        return Gson().toJson(latLong)
    }

    @TypeConverter
    fun toLatLong(latLongString: String) : LatLong{
        val latlongType = object : TypeToken<LatLong>() {}.type
        return Gson().fromJson(latLongString, latlongType)
    }
}