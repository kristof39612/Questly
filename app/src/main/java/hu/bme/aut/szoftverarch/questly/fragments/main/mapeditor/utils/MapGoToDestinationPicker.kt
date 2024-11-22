package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable

@SuppressLint("UnrememberedMutableState")
@Composable
fun MapGoToDestinationPicker(
    createFromScratch: Boolean,
    initialLoc: LatLng,
    taskPointLoc: LatLng,
    updateMarkerLocation: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = if(initialLoc == LatLng(0.0,0.0))
            CameraPosition.fromLatLngZoom(taskPointLoc, 15f) // Initial camera position
        else
            CameraPosition.fromLatLngZoom(initialLoc, 15f) // Initial camera position
    }

    // Observe changes in camera position and call `updateMarkerLocation`
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving && createFromScratch) {
            updateMarkerLocation(cameraPositionState.position.target)
        }
    }
    var uiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false
    )
    if (!createFromScratch) {
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            tiltGesturesEnabled = false,
            rotationGesturesEnabled = false,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map composable
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        ){
            if(!createFromScratch){
                Marker(
                    state = MarkerState(position =  initialLoc),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            R.drawable.ic_tourflag,
                            StatusEnum.PENDING
                        )
                    ),
                )
            } else
                Marker(
                    state = MarkerState(position = cameraPositionState.position.target),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            R.drawable.ic_tourflag,
                            StatusEnum.PENDING
                        )
                    ),
                )
            Marker(
                state = MarkerState(position = taskPointLoc),
                icon = BitmapDescriptorFactory.fromBitmap(
                    getBitmapFromVectorDrawable(
                        context,
                        R.drawable.ic_walking,
                        StatusEnum.PENDING
                    )
                ),
            )
            Circle(
                center = taskPointLoc,
                radius = 1000.0,
                strokeColor = Color.Red,
                strokeWidth = 2f,
                fillColor = Color(0x5500FF00)
            )
        }

    }

}
