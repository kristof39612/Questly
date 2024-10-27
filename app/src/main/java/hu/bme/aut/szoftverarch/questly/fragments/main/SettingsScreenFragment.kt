package hu.bme.aut.szoftverarch.questly.fragments.main

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.tasks.GoToPointTask
import hu.bme.aut.szoftverarch.questly.data.tasks.SingleChoiceTask
import hu.bme.aut.szoftverarch.questly.data.tasks.TextPromptTask
import hu.bme.aut.szoftverarch.questly.data.utils.GsonProvider
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val bpcenter = LatLong(47.4977309, 19.0506962)
    val textpoint = LatLong(47.4977309 + 0.00045, 19.0506962) // ~50m north
    val walkpoint = LatLong(47.4977309, 19.0506962 + 0.00065) // ~50m east
    val selectionpoint = LatLong(47.4977309 - 0.00045, 19.0506962) // ~50m south
    val sampleTask = TextPromptTask()
    val textTask = TextPromptTask()
    val walkTask = GoToPointTask()
    val selectionTask = SingleChoiceTask()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val taskPoints = listOf(
        TaskPoint(id = "Point 1", location = bpcenter, task = sampleTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 4.5f),
        TaskPoint(id = "TextPoint 1", location = textpoint, task = textTask, status = StatusEnum.REJECTED, authorUserId = "sampleUser", rating = 2.5f),
        TaskPoint(id = "WalkPoint 1", location = walkpoint, task = walkTask, status = StatusEnum.PENDING, authorUserId = "sampleUser", rating = 3.55f),
        TaskPoint(id = "SelectionPoint 1", location = selectionpoint, task = selectionTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 0.1f),
    )

    val taskPointDatabase = TaskPointDatabase.getInstance(context)
    val taskPointDao = taskPointDatabase.taskPointDao()
    var tl: List<TaskPoint> = listOf()
    // Initialize Gson with TaskTypeAdapter

    Column(Modifier.padding(16.dp)) {
        Text("Settings Screen")
        Button(onClick = {
            for (taskPoint in taskPoints) { scope.launch {
                taskPointDao.insertAll(taskPoint)
                }
            }
        }) {
            Text("Debug TaskPoints")
        }
        Button(onClick = {
            scope.launch {
                tl = taskPointDao.queryAll()
            }
        }){
            Text("Query TaskPoints")
        }
        Button(onClick = {
            val debugList =tl
            Toast.makeText(context, "TaskPoints", Toast.LENGTH_SHORT).show()
        }) {
            Text("DEBUG TaskPoints")
        }
    }


}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}