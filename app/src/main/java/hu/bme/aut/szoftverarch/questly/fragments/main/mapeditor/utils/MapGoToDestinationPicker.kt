package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
    updateMarkerLocation: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = if(initialLoc == LatLng(0.0,0.0))
            CameraPosition.fromLatLngZoom(LatLng(47.4977309, 19.0506962), 15f) // Initial camera position
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
        }

    }
}
