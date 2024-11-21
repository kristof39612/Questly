package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors.EditGoToPointTaskComposable
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors.EditSingleChoiceTaskComposable
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors.EditTextPromptTaskComposable
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors.taskChecker
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import hu.bme.aut.szoftverarch.questly.graphics.taskTypeIcon
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorFragment(
    navController: NavController,
    location: LatLong = LatLong(0.0, 0.0),
    taskID: String = "-1"
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    val taskPointDao = TaskPointDatabase.getInstance(context).taskPointDao()
    val userRole = context.getSharedPreferences("UserData", 0).getString("userRole", "USER")

    var showProgress by remember { mutableStateOf(false) }
    val taskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    val taskPointLocation = remember { mutableStateOf(LatLong(0.0, 0.0)) }

    val taskTypes = listOf("Text Prompt Task", "Single Choice Task", "Go To a Point Task")
    var selectedTaskType by remember { mutableStateOf("Select a task type from the list...") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    /// Task specific variables
    val question = remember { mutableStateOf("") }
    val textPromptAnswer = remember { mutableStateOf("") }
    var singleChoiceAnswers by remember { mutableStateOf(List(5) { "" }) }
    var singleChoiceCorrectAnswerIndex by remember { mutableIntStateOf(-1) }
    var gotoLocation by remember { mutableStateOf(LatLong(0.0, 0.0)) }
    val bpcenter = LatLng(47.4977309, 19.0506962)
    var finalizeDatSheet by remember { mutableStateOf(false) }

    /// Finalizer values
    val taskPointName = remember { mutableStateOf("") }

    if (showProgress) {
        LoadingDialog("Loading...")
    }

    LaunchedEffect(Unit) {
        if (taskID != "-1") {
            showProgress = true
            val response = apiService.getTaskPointById(taskID)
            if (response.isSuccessful) {
                taskPoint.value = response.body()
                taskPointLocation.value = taskPoint.value!!.location
            }
            showProgress = false
        } else if (location.latitude != 0.0 && location.longitude != 0.0) {
            taskPointLocation.value = location
        }
    }



    if (taskPoint.value != null) {
        Text("TaskLoadedFromServer")
    } else if (taskPointLocation.value.latitude != 0.0 && taskPointLocation.value.longitude != 0.0) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (!finalizeDatSheet) {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    TextField(
                        value = selectedTaskType,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Task type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        leadingIcon = {
                            Image(
                                bitmap = getBitmapFromVectorDrawable(
                                    context,
                                    taskTypeIcon(selectedTaskType)
                                ).asImageBitmap(),
                                contentDescription = "Task type icon",
                                modifier = Modifier
                                    .onGloballyPositioned { coordinates ->
                                        mTextFieldSize = coordinates.size.toSize()
                                    }
                                    .padding(8.dp)
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        taskTypes.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedTaskType = option
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.90f)
                ) {
                    when (selectedTaskType) {
                        "Text Prompt Task" -> EditTextPromptTaskComposable(
                            latLng = taskPointLocation.value.toGoogleLatLong(),
                            question = question.value,
                            answer = textPromptAnswer.value,
                            onQuestionChange = { question.value = it },
                            onAnswerChange = { textPromptAnswer.value = it })

                        "Single Choice Task" -> EditSingleChoiceTaskComposable(
                            latLng = taskPointLocation.value.toGoogleLatLong(),
                            question = question.value,
                            answers = singleChoiceAnswers,
                            correctAnswerIndex = singleChoiceCorrectAnswerIndex,
                            onQuestionChange = { question.value = it },
                            onAnswerChange = { index, newVal ->
                                singleChoiceAnswers = singleChoiceAnswers.toMutableList()
                                    .apply { this[index] = newVal }
                            },
                            onCorrectAnswerChange = { singleChoiceCorrectAnswerIndex = it }
                        )

                        "Go To a Point Task" -> EditGoToPointTaskComposable(
                            onLocationChange = { gotoLocation = it }
                        )
                    }
                }
            } else {        /// Finalizer
                TaskFinalizerComposable(
                    taskPointName = taskPointName.value,
                    onTaskPointNameChanged = { taskPointName.value = it },
                    taskPointLocation = taskPointLocation.value,
                    selectedTaskType = selectedTaskType,
                    gotoLocation = gotoLocation
                )
            }
            if (selectedTaskType != "Select a task type from the list...") {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                        onClick = {
                            try {
                                val finalizedTask = taskChecker(
                                    selectedTaskType,
                                    taskPointLocation.value,
                                    question.value,
                                    textPromptAnswer.value,
                                    singleChoiceAnswers,
                                    singleChoiceCorrectAnswerIndex,
                                    gotoLocation
                                )
                                if(finalizeDatSheet) {
                                    if (taskPointName.value.length < 5 || taskPointName.value.length > 25)
                                        throw IllegalArgumentException("Task Point name must be between 5 and 25 characters")
                                    else {
                                        val createdTP = TaskPoint(
                                            id = 99999,
                                            task = finalizedTask,
                                            status = StatusEnum.PENDING,
                                            location = taskPointLocation.value,
                                            authorUserId = context.getSharedPreferences(
                                                "UserData",
                                                0
                                            ).getString("userID", "-1")!!,
                                            rating = 0.0f,
                                            title = taskPointName.value
                                        )
                                        showProgress = true
                                        scope.launch {
                                            val response = apiService.createTaskPoint(createdTP)
                                            if (response.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    "Task Point created successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.popBackStack()

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to create Task Point",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            taskPointDao.insertAll(response.body()!!)
                                            showProgress = false
                                        }
                                    }
                                } else {
                                    finalizeDatSheet = true
                                }
                            } catch (e: IllegalArgumentException) {
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Submit", color = Color.White)
                    }
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            if(finalizeDatSheet)
                                finalizeDatSheet = false
                            else
                                navController.popBackStack()
                        }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            }


        }

    }
}
