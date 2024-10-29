package hu.bme.aut.szoftverarch.questly.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import hu.bme.aut.szoftverarch.questly.data.entries.LogEntry

@Dao
interface LogEntryDao {

    @Query("SELECT * FROM logentry")
    suspend fun queryAll(): List<LogEntry>

    @Query("SELECT * FROM logentry WHERE id = :id")
    suspend fun queryById(id: String): LogEntry

    @Insert
    suspend fun insertAll(vararg logEntry: LogEntry)

    @Delete
    suspend fun delete(logEntry: LogEntry)

}