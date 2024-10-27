package hu.bme.aut.szoftverarch.questly.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import hu.bme.aut.szoftverarch.questly.data.TaskPoint

@Dao
interface TaskPointDao {

    @Query("SELECT * FROM taskpoints")
    suspend fun queryAll(): List<TaskPoint>

    @Insert
    suspend fun insertAll(vararg taskPoints: TaskPoint)

    @Delete
    suspend fun delete(taskPoint: TaskPoint)

}