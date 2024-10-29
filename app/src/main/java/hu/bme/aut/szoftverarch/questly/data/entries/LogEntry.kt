package hu.bme.aut.szoftverarch.questly.data.entries

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "logentry")
class LogEntry {
    @PrimaryKey
    var id: String = ""
    var visitedPointId: String = ""
    // #TODO: Photo
    var visitDate: String = Date().toString()
    var userId: String = ""
    var givenRating: Int = 0
}