package hu.bme.aut.szoftverarch.questly.data.utils

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import hu.bme.aut.szoftverarch.questly.data.tasks.Task

class Converters {

    @TypeConverter
    fun fromTask(task: Task): String {
        return GsonProvider.gson.toJson(task, Task::class.java)
    }

    @TypeConverter
    fun toTask(taskString: String): Task {
        val taskType = object : TypeToken<Task>() {}.type
        return GsonProvider.gson.fromJson(taskString, taskType)
    }

    @TypeConverter
    fun fromLatlong(latLong: LatLong): String {
        return GsonProvider.gson.toJson(latLong)
    }

    @TypeConverter
    fun toLatLong(latLongString: String): LatLong {
        val latlongType = object : TypeToken<LatLong>() {}.type
        return GsonProvider.gson.fromJson(latLongString, latlongType)
    }

}