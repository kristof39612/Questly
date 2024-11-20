package hu.bme.aut.szoftverarch.questly.data.entries

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "logentry")
class LogEntry {
    @PrimaryKey
    var id: Long = 0
    var visitedPointId: String = ""
    var visitDate: String = Date().toString()
    var userId: String = ""
    var photoId: String = ""
    var givenRating: Int = 0
}