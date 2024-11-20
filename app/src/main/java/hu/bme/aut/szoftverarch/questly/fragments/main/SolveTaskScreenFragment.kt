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
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.networking.*
import hu.bme.aut.szoftverarch.questly.graphics.ConfirmExitDialog
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import java.io.File
import java.io.FileOutputStream
import hu.bme.aut.szoftverarch.questly.graphics.StarIcon
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.util.Locale

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
    val imageFilePath = rememberSaveable { mutableStateOf<String?>(null) }
    val imageBitmap = imageFilePath.value?.let { BitmapFactory.decodeFile(it) }
    var rating by remember { mutableIntStateOf(0) }
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    var showProgress by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // Save the bitmap to a cache file
            val file = File(context.cacheDir, "temp_image.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            imageFilePath.value = file.absolutePath
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
            Toast.makeText(context, "Please grant Camera permission", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(Unit) {
        taskPoint.value = taskPointDao.queryById(taskId)
    }

    if(showProgress){
        LoadingDialog("Submitting task...")
    }

    BackHandler {
        showDialog.value = true
    }

    ConfirmExitDialog(
        showDialog = showDialog,
        onConfirm = {
            scope.launch {
                val cancelRequest = StartStopTaskRequest(taskId.toLong())
                try{
                    val response = apiService.cancelTask(cancelRequest)
                    if(response.isSuccessful){
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, R.string.taskCancelFailed, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception){
                    Toast.makeText(context, R.string.taskCancelFailed, Toast.LENGTH_SHORT).show()
                }
            }
                    },
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
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
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
                        Spacer(modifier = Modifier.height(250.dp))
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
                        .height(180.dp)
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

                // Add a row for the 5 stars rating section
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                        .height(30.dp)
                ) {
                    repeat(5) { index ->
                        Box(modifier = Modifier
                            .clickable {
                                rating = index + 1
                            }
                        ) {
                            StarIcon(
                                size = 40.dp,
                                color = Color(0xFFFF4D01),
                                fillRatio = if (index < rating) 1f else 0f
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))
                Button(
                    onClick = {
                        showProgress = true
                        if (checkIfCorrect(taskPoint.value!!, answer, selectedChoice)) {
                            if (imageFilePath.value != null) {
                                scope.launch {
                                    try {
                                        val file = File(imageFilePath.value!!)
                                        val requestBody =
                                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                        val part = MultipartBody.Part.createFormData(
                                            "photo",
                                            file.name,
                                            requestBody
                                        )
                                        //val completeRequest = CompleteTaskRequest(rating.toLong())
                                        val response =
                                            apiService.completeTask(rating.toLong(), part)
                                        if (response.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Task completed successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.popBackStack()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Task completion failed!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Task completion failed!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally{
                                        showProgress = false
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please take a picture", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(context, "Incorrect answer!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }
                Text(
                    text = "Task ID: $taskId",
                    modifier = Modifier
                        .padding(top = 1.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

fun checkIfCorrect(taskPoint: TaskPoint, answer: String, selectedChoice: Int): Boolean {
    return when (val task = taskPoint.task) {
        is TextPromptTask -> task.answer.lowercase(Locale.ROOT) == answer.lowercase(Locale.ROOT)
        is SingleChoiceTask -> task.correctAnswer == selectedChoice
        is GoToPointTask -> true
        else -> false
    }
}