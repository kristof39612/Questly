package hu.bme.aut.szoftverarch.questly.fragments.main.logentry

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.LogEntryDatabase
import hu.bme.aut.szoftverarch.questly.data.entries.LogEntry
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import hu.bme.aut.szoftverarch.questly.graphics.StarIcon
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun LogEntryDetailedViewFragment(
    navController: NavController,
    logEntryId: String
){
    var showProgress by remember { mutableStateOf(false) }
    val logEntry = remember { mutableStateOf<LogEntry?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val logEntryDatabase = LogEntryDatabase.getInstance(context)
    val logEntryDao = logEntryDatabase.logEntryDao()
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    var icandraw by remember { mutableStateOf(false)}
    val taskpoint = remember { mutableStateOf<TaskPoint?>(null) }

    if(showProgress){
        LoadingDialog("Fetching details...")
    }

    LaunchedEffect(Unit) {
        showProgress = true
        scope.launch {
            logEntry.value = logEntryDao.queryById(logEntryId)
            val response = apiService.getTaskPointById(logEntry.value!!.visitedPointId)
            try{
                if(response.isSuccessful){
                    val tp = response.body()
                    tp?.let {
                        taskpoint.value = it
                    }
                }
            } catch (e: Exception){
                println(e)
                Toast.makeText(context, "{$e}", Toast.LENGTH_SHORT).show()
            } finally {
                showProgress = false
                icandraw = true
            }
        }
    }
    if(icandraw){
        LogEntryDetailedView(logEntry = logEntry.value!!,
            taskPoint = taskpoint.value!!,
            onBackClick = {
            navController.popBackStack()
        })
    }

}

@SuppressLint("UnrememberedMutableState")
@Composable
fun LogEntryDetailedView(
    logEntry: LogEntry,
    taskPoint: TaskPoint,
    onBackClick: () -> Unit
){
    val context = LocalContext.current
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    val photo = remember { mutableStateOf<Bitmap?>(null) }
    var showProgress by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val formatter = SimpleDateFormat("yyyy-MM-dd / HH:mm", Locale.getDefault())
    val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(logEntry.visitDate)

    LaunchedEffect(Unit) {
        scope.launch {
            showProgress = true
            try{
                val response = apiService.getPhoto(logEntry.photoId)
                if(response.isSuccessful){
                    val responsebody = response.body()
                    responsebody?.let {
                        val bytearray = it.bytes()
                        photo.value = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.size)
                    }
                }
            } catch (e: Exception){
                println(e)
                Toast.makeText(context, "{$e}", Toast.LENGTH_SHORT).show()
            } finally {
                showProgress = false
            }
        }
    }

    if(showProgress){
        LoadingDialog("Downloading image...")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Log Entry Details",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        // Placeholder for Photo
        DisplayPhoto(photo.value)
        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        // Log Entry Information
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Point ID
            Text(
                text = "Visited Point ID: ${logEntry.visitedPointId}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Date
            Text(
                text = "Visit Date: ${formatter.format(parsedDate ?: Date())}",
                style = MaterialTheme.typography.bodyMedium
            )

            Row{
                Text(
                    text = "Rating Given: ",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                for (i in 0 until logEntry.givenRating){
                    StarIcon(size = 24.dp, color = MaterialTheme.colorScheme.primary, fillRatio = 1f)
                }
            }
            Text(
                text = "Points Earned: ${taskPoint.task.pointsForCompletion}",
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                       taskPoint.getGoogleLatLng(), 17f)
                },
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = false,
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                    rotationGesturesEnabled = false,
                ),
            ) {
                val icon = when (taskPoint.task::class.java.simpleName) {
                    "TextPromptTask" -> R.drawable.ic_abc
                    "GoToPointTask" -> R.drawable.ic_walking
                    "SingleChoiceTask" -> R.drawable.ic_selection
                    else -> R.drawable.ic_home
                }
                Marker(
                    state = MarkerState(position = taskPoint.getGoogleLatLng()),
                    icon = BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            context,
                            icon
                        )
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}

@Composable
fun DisplayPhoto(bitmap: Bitmap?) {
    if (bitmap != null) {
        Image(
            painter = BitmapPainter(bitmap.asImageBitmap()),
            contentDescription = "Photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp)
        )
    } else {
        Text(
            text = "Photo Placeholder",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(vertical = 16.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                .wrapContentHeight(Alignment.CenterVertically),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}