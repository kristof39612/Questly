package hu.bme.aut.szoftverarch.questly.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import hu.bme.aut.szoftverarch.questly.data.entries.ToplistEntry

@Dao
interface ToplistDao {

    @Query("SELECT * FROM toplist ORDER BY points DESC")
    suspend fun queryAll(): List<ToplistEntry>

    @Insert
    suspend fun insertAll(vararg toplistEntry: ToplistEntry)

    @Delete
    suspend fun delete(toplistEntry: ToplistEntry)

}