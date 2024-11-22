package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import hu.bme.aut.szoftverarch.questly.graphics.taskTypeIcon

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapReviewComposable(
    latLng: LatLng,
    type: String,
    textToAppearOnTop: String = "Review your Task Point's location"
){
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        Text(
            textToAppearOnTop,
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp).align(
                Alignment.CenterHorizontally
            ),
            fontStyle = FontStyle.Italic
        )
        Spacer(modifier = Modifier.height(4.dp))
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp),
            cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    latLng, 17f)
            },
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false,
                compassEnabled = false,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = false,
                rotationGesturesEnabled = false,
            ),
        ) {
            Marker(
                state = MarkerState(position = latLng),
                icon = BitmapDescriptorFactory.fromBitmap(
                    getBitmapFromVectorDrawable(
                        context,
                        taskTypeIcon(type),
                        StatusEnum.PENDING
                    )
                ),
            )
        }
    }
}