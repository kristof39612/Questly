package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import hu.bme.aut.szoftverarch.questly.graphics.getBitmapFromVectorDrawable
import hu.bme.aut.szoftverarch.questly.graphics.taskTypeIcon

@Composable
fun TaskEditorFragment(
    navController: NavController,
    location: LatLong = LatLong(0.0, 0.0),
    taskID: String = "-1"
) {
    val context = LocalContext.current
    val taskPointDao = TaskPointDatabase.getInstance(context).taskPointDao()
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    val userRole = context.getSharedPreferences("UserData", 0).getString("userRole", "USER")
    var showProgress by remember { mutableStateOf(false) }
    val taskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    val taskPointLocation = remember { mutableStateOf(LatLong(0.0, 0.0)) }

    val taskTypes = listOf("Text Prompt Task", "Single Choice Task", "Go To a Point Task")
    var selectedTaskType by remember { mutableStateOf("Select a task type from the list...") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val dropdownIcon =
        if (dropdownExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }


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
        //Text("NewTaskFromLocation")
        Surface(
            modifier = Modifier
                .padding(8.dp)
        ){
            OutlinedTextField(
                value = selectedTaskType,
                onValueChange = { selectedTaskType = it },
                enabled = true,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        // This value is used to assign to
                        // the DropDown the same width
                        mTextFieldSize = coordinates.size.toSize()
                    },
                label = { Text("Task type") },
                trailingIcon = {
                    Icon(dropdownIcon, "contentDescription",
                        Modifier.clickable { dropdownExpanded = !dropdownExpanded })
                },
                leadingIcon = {
                    Image(
                        getBitmapFromVectorDrawable(
                            context,
                            taskTypeIcon(selectedTaskType),
                        ).asImageBitmap(),
                        contentDescription = null
                    )
                },
            )
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                //.offset(x = 16.dp)
            ) {
                taskTypes.forEach { taskType ->
                    DropdownMenuItem(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = { Text(taskType) },
                        onClick = {
                            selectedTaskType = taskType
                            dropdownExpanded = false
                        },
                        leadingIcon = {
                            Image(
                                getBitmapFromVectorDrawable(
                                    context,
                                    taskTypeIcon(taskType),
                                ).asImageBitmap(),
                                contentDescription = null
                            )
                        }
                    )
                }
            }


        }

    }

}