package hu.bme.aut.szoftverarch.questly.fragments.main.logentry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import hu.bme.aut.szoftverarch.questly.data.TaskPoint
import hu.bme.aut.szoftverarch.questly.data.database.LogEntryDatabase
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.entries.LogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogEntryListFragment(
    navController: NavController
) {

    val context = LocalContext.current
    val logEntries = remember { mutableStateListOf<LogEntry>() }
    val logEntryDao = LogEntryDatabase.getInstance(context).logEntryDao()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val entries = logEntryDao.queryAll()
            logEntries.addAll(entries)
        }
    }
    Column(modifier = Modifier.padding(4.dp)) {
        Text(
            "Log entries",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        LogEntryList(
            logEntries = logEntries,
            onLogEntryClick = {
                navController.navigate("logEntry/${it.id}")
                //Toast.makeText(context, "Clicked on ${it.visitedPointId}", Toast.LENGTH_SHORT).show()
            }
        )
    }

}

@Composable
fun LogEntryList(
    logEntries: List<LogEntry>,
    onLogEntryClick: (LogEntry) -> Unit
) {
    if(logEntries.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(logEntries) { logEntry ->
                LogEntryItem(logEntry = logEntry, onClick = { onLogEntryClick(logEntry) })
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xAA87CEEB), RoundedCornerShape(8.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "There are no log entries",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = ":(",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val formatter = SimpleDateFormat("yyyy-MM-dd / HH:mm", Locale.getDefault())
    val parsedDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(logEntry.visitDate)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState().value
    val taskPointDao = TaskPointDatabase.getInstance(LocalContext.current).taskPointDao()
    val taskPoint = remember { mutableStateOf<TaskPoint?>(null) }
    LaunchedEffect(Unit) {
        scope.launch {
            taskPoint.value = taskPointDao.queryById(logEntry.visitedPointId)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(4.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if(isPressed) 16.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer, // Normal state
            contentColor = MaterialTheme.colorScheme.onSurface // Text/content color
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = taskPoint.value?.title ?: "Unknown",
                    //text = "Point ID: ${logEntry.visitedPointId}",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    //text = "Date: ${logEntry.visitDate}",
                    text = formatter.format(parsedDate ?: Date()),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = "Rating: ${logEntry.givenRating}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
