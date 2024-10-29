package hu.bme.aut.szoftverarch.questly.fragments.main


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import hu.bme.aut.szoftverarch.questly.data.database.ToplistDatabase
import hu.bme.aut.szoftverarch.questly.data.tasks.GoToPointTask
import hu.bme.aut.szoftverarch.questly.data.tasks.SingleChoiceTask
import hu.bme.aut.szoftverarch.questly.data.tasks.TextPromptTask
import hu.bme.aut.szoftverarch.questly.data.toplist.ToplistEntry
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.data.utils.StatusEnum
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen() {
    val bpcenter = LatLong(47.4977309, 19.0506962)
    val textpoint = LatLong(47.4977309 + 0.00045, 19.0506962) // ~50m north
    val walkpoint = LatLong(47.4977309, 19.0506962 + 0.00065) // ~50m east
    val selectionpoint = LatLong(47.4977309 - 0.00045, 19.0506962) // ~50m south
    val sampleTask = TextPromptTask(question="What is the capital of Hungary?", answer="Budapest")
    val textTask = TextPromptTask(question = "What is red and blue?", answer = "Purple")
    val walkTask = GoToPointTask(where = bpcenter)
    val selectionTask = SingleChoiceTask(question = "What is the capital of Hungary?", choices = listOf("Budapest", "Vienna", "Berlin", "Prague"), correctAnswer = 0)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hutyra = LatLong(47.503237, 19.075318)

    val taskPoints = listOf(
        TaskPoint(id = "HPoint 1", location = hutyra, task = sampleTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 4.5f),
        TaskPoint(id = "TextPoint 1", location = textpoint, task = textTask, status = StatusEnum.REJECTED, authorUserId = "sampleUser", rating = 2.5f),
        TaskPoint(id = "WalkPoint 1", location = walkpoint, task = walkTask, status = StatusEnum.PENDING, authorUserId = "sampleUser", rating = 3.55f),
        TaskPoint(id = "SelectionPoint 1", location = selectionpoint, task = selectionTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 0.1f),
    )

    val toplistEntries = listOf(
        ToplistEntry(userId = "Alice", earnedPoints = 100),
        ToplistEntry(userId = "Bob", earnedPoints = 200),
        ToplistEntry(userId = "Charlie", earnedPoints = 300),
        ToplistEntry(userId = "Daniel", earnedPoints = 400),
        ToplistEntry(userId = "Emma", earnedPoints = 500),
    )

    val taskPointDatabase = TaskPointDatabase.getInstance(context)
    val taskPointDao = taskPointDatabase.taskPointDao()
    val toplistDatabase = ToplistDatabase.getInstance(context)
    val toplistDao = toplistDatabase.toplistDao()
    // Initialize Gson with TaskTypeAdapter

    Column(Modifier.padding(16.dp)) {
        Text("Debug options")
        Button(onClick = {
            for (taskPoint in taskPoints) { scope.launch {
                taskPointDao.insertAll(taskPoint)
                }
            }
        }) {
            Text("Insert debug TaskPoints")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            for (toplistEntry in toplistEntries) { scope.launch {
                    toplistDao.insertAll(toplistEntry)
                }
            }
        }){
            Text("Insert debug ToplistEntries")
        }
    }


}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}