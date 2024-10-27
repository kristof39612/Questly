package hu.bme.aut.szoftverarch.questly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.utils.Converters

@Database(entities = [TaskPoint::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskPointDatabase : RoomDatabase() {
    abstract fun taskPointDao(): TaskPointDao

    companion object {
        @Volatile
        private var INSTANCE: TaskPointDatabase? = null

        fun getInstance(context: Context): TaskPointDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskPointDatabase::class.java,
                    "task_point_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}