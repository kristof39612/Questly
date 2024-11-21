package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import hu.bme.aut.szoftverarch.questly.graphics.StarRating
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import hu.bme.aut.szoftverarch.questly.graphics.taskPointIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapEditorFragment(
    navController: NavController
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val bpcenter = LatLng(47.4977309, 19.0506962)
    val taskPointDatabase = TaskPointDatabase.getInstance(context)
    val taskPointDao = taskPointDatabase.taskPointDao()
    val taskPoints = remember { mutableStateListOf<TaskPoint>() }
    val selectedTaskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showProgress by remember { mutableStateOf(false) }
    val tempMarkerPosition = remember { mutableStateOf<LatLng?>(null) }
    val userRole = context.getSharedPreferences("UserData", 0).getString("userRole", "USER")

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bpcenter, 15f)
    }
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = true)
    }
    val mapProperties = remember {
        MapProperties(isMyLocationEnabled = true)
    }

    if(showProgress){
        LoadingDialog()
    }

    LaunchedEffect(Unit) {
        showProgress = true
        scope.launch {
            val points = taskPointDao.queryAll()
            taskPoints.addAll(points)
            showProgress = false
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        ModernMapEditorBox()
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = uiSettings,
            onMapClick = { latLng ->
                tempMarkerPosition.value = latLng
            }
        ) {
            taskPoints.forEach { taskPoint ->
                val icon = taskPointIcon(taskPoint)

                Marker(
                    state = MarkerState(position = taskPoint.getGoogleLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            icon,
                            taskPoint.status
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

            // Temporary marker IF SET
            tempMarkerPosition.value?.let { tempPosition ->
                Marker(
                    state = MarkerState(position = tempPosition),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            R.drawable.ic_selectedpoint,
                            StatusEnum.PENDING
                        )
                    )
                )
            }
        }

        selectedTaskPoint.value?.let { taskPoint ->
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
                    if(taskPoint.status == StatusEnum.APPROVED)
                        StarRating(rating = taskPoint.rating)
                    else
                        Text(stringResource(R.string.statusLabel) + " ${when(taskPoint.status){
                            StatusEnum.APPROVED -> "Approved"
                            StatusEnum.PENDING -> "Pending"
                            StatusEnum.REJECTED -> "Rejected"
                        }}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(4.dp))
                    Text(stringResource(R.string.taskIdLabel) + " ${taskPoint.id}")
                    //Text(stringResource(R.string.locationLabel) +" ${taskPoint.location.latitude}, ${taskPoint.location.longitude}", fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Author User ID: ${taskPoint.authorUserId}")
                    Spacer(modifier = Modifier.height(8.dp))

                    if(userRole == "ADMIN"){
                        Button(
                            onClick = {
                                scope.launch {
                                    sheetState.hide()
                                    selectedTaskPoint.value = null
                                }
                                navController.navigate("edit/taskpoint/${taskPoint.id}")
                            },
                        ) {
                            Text("Edit")
                        }
                    }
                }
            }
        }

        // Show dialog if a temporary marker is placed
        tempMarkerPosition.value?.let { tempPosition ->
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {
                    tempMarkerPosition.value = null // Remove marker on dismissal
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Create a task point here?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                       modifier = Modifier
                           .padding(4.dp)
                            .fillMaxWidth(0.6f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Task point creation started!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                tempMarkerPosition.value = null // Remove marker
                                navController.navigate("edit/newTaskpoint/${tempPosition.latitude},${tempPosition.longitude}")
                                scope.launch { sheetState.hide() }
                            }
                        ) {
                            Text("Yes")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                tempMarkerPosition.value = null // Remove marker
                                scope.launch { sheetState.hide() }
                            }
                        ) {
                            Text("No")
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun ModernMapEditorBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp) // Adjust height as needed
            .background(Color.Gray)
            .border(BorderStroke(2.dp, Color.DarkGray))
    ) {
        // Drawing the slashes background
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val dashWidth = 15f
            val dashHeight = 5f
            val gap = 10f

            for (y in 0..size.height.toInt() step (dashHeight.toInt() + gap.toInt())) {
                for (x in 0..size.width.toInt() step (dashWidth.toInt() + gap.toInt())) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(x.toFloat(), y.toFloat()),
                        end = Offset(x + dashWidth, y + dashHeight),
                        strokeWidth = 3f
                    )
                }
            }
        }

        // Text in the center
        Text(
            text = "Map Editor",
            color = Color.Red,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            modifier = Modifier.align(Alignment.Center)
                .padding(4.dp)
                .background(
                    color = Color(0xAA000000), // Semi-transparent background for contrast
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 5.dp, vertical = 0.dp)
        )
    }
}
