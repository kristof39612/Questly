package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils.MapGoToDestinationPicker

@Composable
fun EditGoToPointTaskComposable(
    onLocationChange: (LatLong) -> Unit,
) {
    Column(modifier = Modifier.padding(4.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Specify the location you want the players to go to",
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.padding(2.dp))
        MapGoToDestinationPicker(updateMarkerLocation = {
            onLocationChange(LatLong(it.latitude, it.longitude))
        })
    }
}