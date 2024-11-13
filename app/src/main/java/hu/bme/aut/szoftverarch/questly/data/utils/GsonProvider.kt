// GsonProvider.kt
package hu.bme.aut.szoftverarch.questly.data.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.bme.aut.szoftverarch.questly.data.tasks.Task
import retrofit2.converter.gson.GsonConverterFactory

object GsonProvider {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Task::class.java, TaskTypeAdapter())
        .create()
}

fun gcf(): GsonConverterFactory {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(Task::class.java, TaskTypeAdapter())
    return GsonConverterFactory.create(gsonBuilder.create())
}
