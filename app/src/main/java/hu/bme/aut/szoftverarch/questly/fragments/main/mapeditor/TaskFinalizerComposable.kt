package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils.MapReviewComposable

@Composable
fun TaskFinalizerComposable(
    taskPointName: String,
    onTaskPointNameChanged: (String) -> Unit,
    taskPointLocation: LatLong,
    selectedTaskType: String,
    gotoLocation: LatLong
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Text(
            "Specify a name for your Task Point",
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            fontStyle = FontStyle.Italic
        )
        OutlinedTextField(
            value = taskPointName,
            onValueChange = {
                onTaskPointNameChanged(it)
            },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        // Google Maps preview
        MapReviewComposable(
            latLng = taskPointLocation.toGoogleLatLong(),
            type = selectedTaskType
        )
        if(selectedTaskType == "Go To a Point Task") {
            MapReviewComposable(
                latLng = gotoLocation.toGoogleLatLong(),
                type = "Go To a Point Task",
                textToAppearOnTop = "Review the location the players will be directed to"
            )
        }
    }
}