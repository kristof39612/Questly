package hu.bme.aut.szoftverarch.questly.fragments.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hu.bme.aut.szoftverarch.questly.ConfirmExitDialog
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.tasks.*
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.Manifest
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SolveTaskScreen(navController: NavController, taskId: String) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    val taskPointDatabase = remember { TaskPointDatabase.getInstance(context) }
    val taskPointDao = remember { taskPointDatabase.taskPointDao() }
    val scope = rememberCoroutineScope()
    val taskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    var answer by remember { mutableStateOf("") }
    var selectedChoice by remember { mutableIntStateOf(-1) }
    val camPermissionState =  rememberPermissionState(Manifest.permission.CAMERA)
    var imageFilePath by rememberSaveable { mutableStateOf<String?>(null) }
    val imageBitmap = imageFilePath?.let { BitmapFactory.decodeFile(it) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Save the bitmap to a cache file
            val file = File(context.cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            imageFilePath = file.absolutePath
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(camPermissionState) {
        if (!camPermissionState.status.isGranted && camPermissionState.status.shouldShowRationale) {
            Toast.makeText(context, "Please grant Camera permission", Toast.LENGTH_SHORT)
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            taskPoint.value = taskPointDao.queryById(taskId)
        }
    }

    BackHandler {
        showDialog.value = true
    }

    ConfirmExitDialog(
        showDialog = showDialog,
        onConfirm = { navController.popBackStack() },
        onDismiss = { showDialog.value = false }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Display task-specific details
                when (val task = taskPoint.value?.task) {
                    is TextPromptTask -> {
                        Text(
                            text = "Question:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.question,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = answer,
                            onValueChange = { answer = it },
                            label = { Text("Your Answer") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                    is SingleChoiceTask -> {
                        Text(
                            text = "Question:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.question,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column {
                            task.choices.forEachIndexed { index, choice ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedChoice = index }
                                        .padding(vertical = 2.dp)
                                ) {
                                    RadioButton(
                                        selected = index == selectedChoice,
                                        onClick = { selectedChoice = index }
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = choice,
                                        modifier = Modifier.clickable { selectedChoice = index }
                                    )
                                }
                            }
                        }
                    }
                    is GoToPointTask -> {
                        Text("Destination: ${task.where.latitude}, ${task.where.longitude}")
                    }
                    else -> {
                        Text("Unknown task type")
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = Color.Gray)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray)
                        .clickable {
                            if(camPermissionState.status.isGranted){
                                launcher.launch(null)
                            //Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
                            } else {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {

                        Text("Tap to take a picture", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Handle submit action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }
                Text(
                    text = "Task ID: $taskId",
                    modifier = Modifier
                        .padding(top = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}