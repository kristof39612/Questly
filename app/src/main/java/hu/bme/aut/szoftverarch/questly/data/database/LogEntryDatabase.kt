package hu.bme.aut.szoftverarch.questly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.bme.aut.szoftverarch.questly.data.entries.LogEntry

@Database(entities = [LogEntry::class], version = 1)
abstract class LogEntryDatabase : RoomDatabase() {
    abstract fun logEntryDao(): LogEntryDao

    companion object {
        @Volatile
        private var INSTANCE: LogEntryDatabase? = null

        fun getInstance(context: Context): LogEntryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogEntryDatabase::class.java,
                    "logentry_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}