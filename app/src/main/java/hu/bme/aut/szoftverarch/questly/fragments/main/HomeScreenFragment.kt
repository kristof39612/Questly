package hu.bme.aut.szoftverarch.questly.fragments.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import kotlinx.coroutines.launch


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Marker
import hu.bme.aut.szoftverarch.questly.fragments.animation.StarRating


@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenFragment(navController: NavController) {
    val context = LocalContext.current
    val taskPoints = remember { mutableStateListOf<TaskPoint>() }
    val taskPointDatabase = remember { TaskPointDatabase.getInstance(context) }
    val taskPointDao = remember { taskPointDatabase.taskPointDao() }
    val scope = rememberCoroutineScope()
    val selectedTaskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isWithinRange by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            val points = taskPointDao.queryAll()
            taskPoints.addAll(points)
        }
    }

    val gpsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val bpcenter = LatLng(47.4977309, 19.0506962)

    // Initial permission request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
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
        MapProperties(isMyLocationEnabled = true)
    }

    if (gpsPermissionState.status.isGranted) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                userLocation = location
            }
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties
        ) {
            taskPoints.forEach { taskPoint ->
                val icon = when (taskPoint.task::class.java.simpleName) {
                    "TextPromptTask" -> R.drawable.ic_abc
                    "GoToPointTask" -> R.drawable.ic_walking
                    "SingleChoiceTask" -> R.drawable.ic_selection
                    else -> R.drawable.ic_home
                }

                Marker(
                    state = MarkerState(position = taskPoint.getGoogleLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(context, icon)),
                    onClick = {
                        selectedTaskPoint.value = taskPoint
                        scope.launch {
                            sheetState.show() // Show the bottom sheet
                        }
                        true
                    }
                )
            }
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
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text("Grant permission")
            }
        }
    }

    // ModalBottomSheet for task details
    selectedTaskPoint.value?.let { taskPoint ->
        LaunchedEffect(userLocation) {
            userLocation?.let { location ->
                val taskLocation = Location("").apply {
                    latitude = taskPoint.location.latitude
                    longitude = taskPoint.location.longitude
                }
                val distance = location.distanceTo(taskLocation)
                isWithinRange = distance <= 300          // In meters -> Specification #TODO: 300m-ről átteni
            }
        }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    selectedTaskPoint.value = null
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = taskPoint.task::class.java.simpleName,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRating(rating = taskPoint.rating)
                // Display task-specific details
                /*when (val task = taskPoint.task) {
                    is TextPromptTask -> {
                        Text("Question: ${task.question}")
                        Text("Answer: ${task.answer}")
                    }
                    is GoToPointTask -> {
                        Text("Destination: ${task.where.latitude}, ${task.where.longitude}")
                    }
                    is SingleChoiceTask -> {
                        Text("Question: ${task.question}")
                        task.choices.forEachIndexed { index, answer ->
                            Text("Option ${index + 1}: $answer")
                        }
                        Text("Correct Answer: ${task.correctAnswer + 1}")
                    }
                    else -> {
                        Text("Unknown task type")
                    }
                }*/
                Text("Task ID: ${taskPoint.id}")
                Text("Location: ${taskPoint.location.latitude}, ${taskPoint.location.longitude}")

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        //Toast.makeText(context, "OK!", Toast.LENGTH_SHORT).show()
                        scope.launch {
                            sheetState.hide()
                            selectedTaskPoint.value = null
                        }
                        navController.navigate("solveTask/${taskPoint.id}")
                    },
                    enabled = isWithinRange
                ) {
                    Text("Start")
                }
            }
        }
    }
}