package hu.bme.aut.szoftverarch.questly.fragments.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import hu.bme.aut.szoftverarch.questly.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreenFragment() {

    val context = LocalContext.current

    val gpsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val bpcenter = LatLng(47.4977309, 19.0506962)
    val textpoint = LatLng(47.4977309 + 0.00045, 19.0506962) // ~50m north
    val walkpoint = LatLng(47.4977309, 19.0506962 + 0.00065) // ~50m east
    val selectionpoint = LatLng(47.4977309 - 0.00045, 19.0506962) // ~50m south


    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(gpsPermissionState) {
        if (!gpsPermissionState.status.isGranted && gpsPermissionState.status.shouldShowRationale) {
            Toast.makeText(context, "Please grant GPS location permission", Toast.LENGTH_SHORT)
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bpcenter, 15f)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true))
    }

    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = true,
        )
    }

    // Initial permission request

    // UI

    if (gpsPermissionState.status.isGranted) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties
        ){
            MarkerComposable(
                state = MarkerState(position = bpcenter)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_tourflag),
                    modifier = Modifier.background(Color.Green),
                    contentDescription = "" ,
                    tint = Color.Black
                )
            }

            MarkerComposable(
                state = MarkerState(position = textpoint)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_abc),
                    modifier = Modifier.background(Color.Black),
                    contentDescription = "" ,
                    tint = Color.White
                )
            }

            MarkerComposable(
                state = MarkerState(position = selectionpoint)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_selection),
                    modifier = Modifier.background(Color.Black),
                    contentDescription = "" ,
                    tint = Color.White
                )
            }
            //val wpstate = MarkerState(position = walkpoint)
            /*MarkerComposable(
                state = wpstate,

                onInfoWindowClick = {marker ->
                    Toast.makeText(context, "Walking", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_walking),
                    modifier = Modifier.background(Color.Black),
                    contentDescription = "" ,
                    tint = Color.White
                )
            }*/
            MarkerInfoWindow(
                state = MarkerState(position = walkpoint),
                icon = BitmapDescriptorFactory.fromResource(R.drawable.placeholder_walking_black)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .border(
                            BorderStroke(1.dp, Color.Black),
                            RoundedCornerShape(10)
                        )
                        .clip(RoundedCornerShape(10))
                        .background(Color.Blue)
                        .padding(20.dp)
                ) {
                    Text("Walking point name", fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Description etc", fontWeight = FontWeight.Medium, color = Color.White)
                }
            }
            /*MarkerInfoWindow(state = wpstate) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("Walk here", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(onClick = {
                        Toast.makeText(context, "Walking", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Start walking")
                    }
                }
            }*/

        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_cat),
                contentDescription = "Location icon",
                modifier = Modifier
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Grant permission")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenFragment()
}