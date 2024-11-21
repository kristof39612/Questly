package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.utils

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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable

@Composable
fun MapGoToDestinationPicker(
    updateMarkerLocation: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(47.4977309, 19.0506962), 15f) // Initial camera position
    }

    // Observe changes in camera position and call `updateMarkerLocation`
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            updateMarkerLocation(cameraPositionState.position.target)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map composable
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        )

        // Static Marker Overlay
        MarkerOverlay(
            context = context,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MarkerOverlay(
    context: Context,
    modifier: Modifier = Modifier
) {
    // Placeholder for a custom marker overlay
    Image(
        bitmap = getBitmapFromVectorDrawable(context, R.drawable.ic_tourflag,StatusEnum.APPROVED).asImageBitmap(),
        contentDescription = null,
        modifier = Modifier.size(24.dp).then(modifier)
    )
}

