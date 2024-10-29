package hu.bme.aut.szoftverarch.questly.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.bme.aut.szoftverarch.questly.data.toplist.ToplistEntry

@Database(entities = [ToplistEntry::class], version = 1)
abstract class ToplistDatabase : RoomDatabase() {
    abstract fun toplistDao(): ToplistDao

    companion object {
        @Volatile
        private var INSTANCE: ToplistDatabase? = null

        fun getInstance(context: Context): ToplistDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToplistDatabase::class.java,
                    "toplist_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}