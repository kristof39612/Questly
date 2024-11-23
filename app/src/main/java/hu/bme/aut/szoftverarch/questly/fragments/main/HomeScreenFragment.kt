package hu.bme.aut.szoftverarch.questly.fragments.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import hu.bme.aut.szoftverarch.questly.data.database.LogEntryDatabase
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.data.networking.StartStopTaskRequest
import hu.bme.aut.szoftverarch.questly.data.tasks.GoToPointTask
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import hu.bme.aut.szoftverarch.questly.graphics.StarRating
import hu.bme.aut.szoftverarch.questly.graphics.TaskCompletionDialog
import hu.bme.aut.szoftverarch.questly.graphics.createHole
import hu.bme.aut.szoftverarch.questly.graphics.createOuterBounds
import hu.bme.aut.szoftverarch.questly.graphics.taskPointIcon


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenFragment(navController: NavController) {
    val context = LocalContext.current
    val taskPoints = remember { mutableStateListOf<TaskPoint>() }
    val taskPointDatabase = remember { TaskPointDatabase.getInstance(context) }
    val taskPointDao = remember { taskPointDatabase.taskPointDao() }
    val logentryDatabase = remember { LogEntryDatabase.getInstance(context) }
    val logEntryDao = remember { logentryDatabase.logEntryDao() }
    val userVisitedPoints = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    val selectedTaskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isWithinRange by remember { mutableStateOf(false) }
    val gpsPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val bpcenter = LatLng(47.4977309, 19.0506962)
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    var showProgress by remember { mutableStateOf(false) }
    var currentTaskpointId by remember { mutableStateOf("") }
    var currentTaskPoint by remember { mutableStateOf<TaskPoint?>(null) }
    var reloadTrigger by remember { mutableStateOf(false) }
    var showTaskCompleteDialog by remember { mutableStateOf(false) }
    var goalInRange by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bpcenter, 15f)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = true))
    }

    val mapProperties = remember {
        MapProperties(isMyLocationEnabled = true)
    }
    LaunchedEffect(Unit) {
        val sp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        currentTaskpointId = sp.getString("currentTask", "") ?: ""
        scope.launch {
            val points = taskPointDao.queryAll()
            userVisitedPoints.clear()
            userVisitedPoints.addAll(logEntryDao.queryByUserId(context.getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("userID", "") ?: ""))
            taskPoints.addAll(points)
        }
    }

    LaunchedEffect(currentTaskpointId) {
        scope.launch {
            currentTaskPoint = taskPointDao.queryById(currentTaskpointId)
        }
    }

    LaunchedEffect(reloadTrigger) {
        if(reloadTrigger) {
            scope.launch {
                taskPoints.clear()
                val points = taskPointDao.queryAll()
                taskPoints.addAll(points)
            }
            val sp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
            sp.edit().putString("currentTask", "").apply()
            currentTaskpointId = sp.getString("currentTask", "")?:""
            currentTaskPoint = null
            scope.launch {
                currentTaskPoint = taskPointDao.queryById(currentTaskpointId)
            }
        } else {
            reloadTrigger = false
        }
    }

    if(showProgress){
        LoadingDialog()
    }

    if(showTaskCompleteDialog){
        TaskCompletionDialog(
            onDismiss = {
                showTaskCompleteDialog = false
            },
            onConfirm = {
                showTaskCompleteDialog = false
                navController.navigate("solveTask/${currentTaskpointId}")
                reloadTrigger = true
            }
        )
    }

    // Initial permission request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, R.string.PermissionDenied, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(gpsPermissionState) {
        if (!gpsPermissionState.status.isGranted && gpsPermissionState.status.shouldShowRationale) {
            Toast.makeText(context, R.string.gpsbegging, Toast.LENGTH_SHORT)
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (gpsPermissionState.status.isGranted) {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                userLocation = location
            }
        }
        if(currentTaskpointId != ""){
            // Overlay Login UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CurrentTaskActiveBox(currentTaskpoint = currentTaskpointId, triggerReload = {
                    reloadTrigger = true
                })
            }
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties
        ) {
            if(currentTaskpointId == "") {
                taskPoints.forEach { taskPoint ->
                    if(taskPoint.status == StatusEnum.APPROVED && !userVisitedPoints.contains(taskPoint.id.toString())){
                        val icon = taskPointIcon(taskPoint)

                        Marker(
                            state = MarkerState(position = taskPoint.getGoogleLatLng()),
                            icon = BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(
                                    context,
                                    icon
                                )
                            ),
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
            } else if (currentTaskPoint != null) {
                val icon = when (currentTaskPoint!!.task::class.java.simpleName) {
                    "TextPromptTask" -> R.drawable.ic_abc
                    "GoToPointTask" -> R.drawable.ic_walking
                    "SingleChoiceTask" -> R.drawable.ic_selection
                    else -> R.drawable.ic_home
                }

                Marker(
                    state = MarkerState(position = currentTaskPoint!!.getGoogleLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            icon
                        )
                    ),
                    onClick = {
                        selectedTaskPoint.value = currentTaskPoint!!
                        scope.launch {
                            sheetState.show() // Show the bottom sheet
                        }
                        true
                    }
                )
                if(currentTaskPoint!!.task::class.java.simpleName == "GoToPointTask"){
                    val gtp = currentTaskPoint!!.task as GoToPointTask

                    LaunchedEffect(userLocation) {
                        userLocation?.let { location ->
                            val taskLocation = Location("").apply {
                                latitude = gtp.where.latitude
                                longitude = gtp.where.longitude
                            }
                            val distance = location.distanceTo(taskLocation)
                            goalInRange =
                                distance <= 30000          // In meters -> Specification #TODO: 300m-ről átteni
                        }
                    }
                    Marker(
                        state = MarkerState(position = LatLng(gtp.where.latitude, gtp.where.longitude)),
                        icon = BitmapDescriptorFactory.fromBitmap(
                            getBitmapFromVectorDrawable(
                                context,
                                R.drawable.ic_tourflag
                            )
                        ),
                        onClick = {
                            if(goalInRange){
                                showTaskCompleteDialog = true
                                true
                            } else {
                                Toast.makeText(
                                    context,
                                    "You are not close enough to the goal!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            }
                        },
                    )
                }
            }
            /// GEOFENCE
            Polygon(
                points = createOuterBounds(),
                holes = listOf((createHole(bpcenter, 10000))),
                fillColor = Color(0x55FF0000),
                strokeWidth = 0f
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.nogps),
                contentDescription = "Location icon",
                modifier = Modifier.padding(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }) {
                Text(stringResource(R.string.grantPermission))
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
                isWithinRange = distance <= 30000          // In meters -> Specification #TODO: 300m-ről átteni
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
                    text = taskPoint.title,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                StarRating(rating = taskPoint.rating)
                Text(stringResource(R.string.taskIdLabel) + " ${taskPoint.id}")
                Text(stringResource(R.string.locationLabel)+": ${taskPoint.location.latitude}, ${taskPoint.location.longitude}")

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showProgress = true
                        scope.launch {
                            sheetState.hide()
                            selectedTaskPoint.value = null
                            val req = StartStopTaskRequest(taskPoint.id)
                            try{
                                val response = apiService.startTask(req)
                                if(response.isSuccessful){
                                    if(taskPoint.task is GoToPointTask){
                                        Toast.makeText(context,"Task Accepted!",Toast.LENGTH_SHORT).show()
                                        val sp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                                        sp.edit().putString("currentTask", taskPoint.id.toString()).apply()
                                        currentTaskpointId = taskPoint.id.toString()
                                    } else {
                                        navController.navigate("solveTask/${taskPoint.id}")
                                    }
                                } else {
                                    Toast.makeText(context, R.string.taskStartFailed, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, R.string.taskStartFailed, Toast.LENGTH_SHORT).show()
                            } finally {
                                showProgress = false
                            }
                        }
                    },
                    enabled = isWithinRange && currentTaskpointId == "",
                ) {
                    Text(stringResource(R.string.start))
                }
            }
        }
    }
}

@Composable
fun CurrentTaskActiveBox(
    currentTaskpoint: String,
    triggerReload: () -> Unit
) {
    val context = LocalContext.current
    val taskPointDatabase = remember { TaskPointDatabase.getInstance(context) }
    val taskPointDao = remember { taskPointDatabase.taskPointDao() }
    val currentTp = remember { mutableStateOf<TaskPoint?>(null) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        val tp = taskPointDao.queryById(currentTaskpoint)
        currentTp.value = tp
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texts on the left
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Active Task: ${currentTp.value?.title ?: "N/A"}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Task ID: ${currentTp.value?.id ?: "N/A"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                // Button with icon on the right
                Button(
                    onClick = {
                        scope.launch {
                            val cancelRequest = StartStopTaskRequest(currentTaskpoint.toLong())
                            try{
                                val apiService = RetrofitInstance.getAuthorizedApi(context)
                                val response = apiService.cancelTask(cancelRequest)
                                if(response.isSuccessful){
                                    Toast.makeText(context, "Task has been cancelled!", Toast.LENGTH_SHORT).show()
                                    triggerReload()
                                } else {
                                    Toast.makeText(context, R.string.taskCancelFailed, Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception){
                                Toast.makeText(context, R.string.taskCancelFailed, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    contentPadding = PaddingValues(0.dp) // Remove extra padding
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_deleteforever),
                        contentDescription = "Delete",
                        tint = Color.White // Ensure icon is white
                    )
                }
            }
        }
    }

}
