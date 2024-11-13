package hu.bme.aut.szoftverarch.questly.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.szoftverarch.questly.data.tasks.Task
import hu.bme.aut.szoftverarch.questly.data.utils.Converters
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum

@Entity(tableName = "taskpoints")
@TypeConverters(Converters::class)
class TaskPoint(
    @PrimaryKey val id: Long,
    var task: Task,
    var status: StatusEnum,
    var location: LatLong,
    var authorUserId: String,
    var rating: Float,
    var title: String,
){
    fun getGoogleLatLng(): LatLng {
        return LatLng(location.latitude, location.longitude)
    }
}
