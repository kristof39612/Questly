package hu.bme.aut.szoftverarch.questly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.bme.aut.szoftverarch.questly.data.TaskPoint

@Database(entities = [TaskPoint::class], version = 1)
abstract class TaskPointDatabase : RoomDatabase() {
    abstract fun taskPointDao(): TaskPointDao

    companion object {
        const val DATABASE_NAME = "taskpoint_database"
        private var INSTANCE: TaskPointDatabase? = null

        fun getInstance(context: Context): TaskPointDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, TaskPointDatabase::class.java,
                    "$DATABASE_NAME.db"
                ).build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }
}