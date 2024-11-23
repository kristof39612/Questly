package hu.bme.aut.szoftverarch.questly.fragments.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.database.LogEntryDatabase
import hu.bme.aut.szoftverarch.questly.data.entries.LogEntry
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.fragments.main.logentry.LogEntryList
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val logEntries = remember { mutableStateListOf<LogEntry>() }
    val logEntryDao = LogEntryDatabase.getInstance(context).logEntryDao()
    val userdata = remember { mutableMapOf<String,String>() }
    var showProgress by remember { mutableStateOf(false) }
    val apiService = RetrofitInstance.getAuthorizedApi(context)

    LaunchedEffect(Unit) {
        showProgress = true
        withContext(Dispatchers.IO) {
            val entries = logEntryDao.queryAll()
            logEntries.addAll(entries)
        }
        scope.launch {
            val sharedPref = context.getSharedPreferences("UserData", 0)
            try {
                val useridresp = apiService.getUserId()
                if (useridresp.isSuccessful) {
                    val userid = useridresp.body()
                    if (userid != null) {
                        userdata["userid"] = userid.toString()
                    }
                }
                val pointsresp = apiService.getUserPoints()
                if (pointsresp.isSuccessful) {
                    val rpbody = pointsresp.body()
                    if (rpbody != null) {
                        userdata["points"] = rpbody.points.toString()
                        userdata["username"] = rpbody.username
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error communicating with server", Toast.LENGTH_SHORT).show()
            } finally {
                userdata["email"] = sharedPref.getString("userEmail", "root@localhost")!!
                showProgress = false
            }
        }
    }

    if (showProgress) {
        LoadingDialog()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
    ) {
        // Background Section
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.raw.geometric)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "Geometric background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()  // Ensures the image fills the entire background
                .zIndex(0f)  // Ensure it's placed behind the content
        )

        // Content Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        ) {
            // Header Section with Background Box for visibility
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xAA0000FF), shape = MaterialTheme.shapes.medium) // Semi-transparent blue background
                    .padding(16.dp),  // Padding inside the background box
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = userdata["username"] ?: "Anonymus", style = MaterialTheme.typography.headlineMedium, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${userdata["points"] ?: "0"} points", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "I've already been to..." Text with Background Box
            Text(
                "I've already been to...",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color(0xAA0000FF), shape = MaterialTheme.shapes.medium) // Semi-transparent blue background
                    .padding(8.dp)
                    .zIndex(1f) // Ensures it appears above the background
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Orange Box for Log Entries
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f)
                    .background(color = Color.Transparent, shape = MaterialTheme.shapes.medium)
            ) {
                LogEntryList(logEntries, onLogEntryClick = {
                    navController.navigate("logEntry/${it.id}")
                })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Section with Background Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(8.dp)
                    .background(Color.LightGray),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "UserID: ${userdata["userid"] ?: "UNKNOWN"}", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = userdata["email"] ?: "root@localhost",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Blue
                )
            }
        }
    }
}

