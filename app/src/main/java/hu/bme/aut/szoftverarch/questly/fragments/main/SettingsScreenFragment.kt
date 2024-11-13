package hu.bme.aut.szoftverarch.questly.fragments.main


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.database.ToplistDatabase
import hu.bme.aut.szoftverarch.questly.data.tasks.GoToPointTask
import hu.bme.aut.szoftverarch.questly.data.tasks.SingleChoiceTask
import hu.bme.aut.szoftverarch.questly.data.tasks.TextPromptTask
import hu.bme.aut.szoftverarch.questly.data.entries.ToplistEntry
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
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
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    var showProgress by remember { mutableStateOf(false) }

    val taskPoints = listOf(
        TaskPoint(id = 1, location = selectionpoint, task = sampleTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 4.5f, title = "XD"),
        TaskPoint(id = 2, location = textpoint, task = textTask, status = StatusEnum.REJECTED, authorUserId = "sampleUser", rating = 2.5f,title = "XD"),
        TaskPoint(id = 3, location = walkpoint, task = walkTask, status = StatusEnum.PENDING, authorUserId = "sampleUser", rating = 3.55f, title = "XD"),
        TaskPoint(id = 4, location = hutyra, task = selectionTask, status = StatusEnum.APPROVED, authorUserId = "sampleUser", rating = 0.1f, title = "XD"),
    )

    val toplistEntries = listOf(
        ToplistEntry(username = "Alice", points = 100),
        ToplistEntry(username = "Bob", points = 200),
        ToplistEntry(username = "Charlie", points = 300),
        ToplistEntry(username = "Daniel", points = 400),
        ToplistEntry(username = "Emma", points = 500),
    )

    val taskPointDatabase = TaskPointDatabase.getInstance(context)
    val taskPointDao = taskPointDatabase.taskPointDao()
    val toplistDatabase = ToplistDatabase.getInstance(context)
    val toplistDao = toplistDatabase.toplistDao()

    Column(Modifier.padding(16.dp)) {

        if (showProgress) {
            Dialog(onDismissRequest = { }) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(150.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Waiting...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

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
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            showProgress = true
            scope.launch {
                try {

                    val response = apiService.getTaskPointById("2")
                    if (response.isSuccessful) {
                        showProgress = false
                        Toast.makeText(
                            context,
                            response.body()?.title ?: "No message",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    showProgress = false
                }
            }
        }) {
            Text("Query TaskPoint by ID")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            showProgress = true
            for (taskPoint in taskPoints) {
                scope.launch {
                    try {
                        val response = apiService.createTaskPoint(taskPoint)
                        if (response.isSuccessful) {
                            showProgress = false
                            Toast.makeText(
                                context,
                                response.body()?.title ?: "No message",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } finally {
                        showProgress = false
                    }
                }
            }
        }) {
            Text("Upload task points")
        }
        Text("User Token: ${context.getSharedPreferences("UserData", Context.MODE_PRIVATE).getString("userToken", "No token")}")
    }


}

@Preview
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen()
}