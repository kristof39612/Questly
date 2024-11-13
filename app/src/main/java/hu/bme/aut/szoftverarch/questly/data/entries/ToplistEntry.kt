package hu.bme.aut.szoftverarch.questly.data.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toplist")
class ToplistEntry(
    @PrimaryKey val username: String,
    var points: Int,
)