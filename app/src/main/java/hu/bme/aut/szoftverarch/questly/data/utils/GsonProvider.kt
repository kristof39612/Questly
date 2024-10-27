// GsonProvider.kt
package hu.bme.aut.szoftverarch.questly.data.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.bme.aut.szoftverarch.questly.data.tasks.Task

object GsonProvider {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Task::class.java, TaskTypeAdapter())
        .create()
}
