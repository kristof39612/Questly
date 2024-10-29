package hu.bme.aut.szoftverarch.questly.data.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toplist")
class ToplistEntry(
    @PrimaryKey val userId: String,
    var earnedPoints: Int,
)