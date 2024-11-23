package hu.bme.aut.szoftverarch.questly.fragments.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.database.ToplistDatabase
import hu.bme.aut.szoftverarch.questly.data.entries.ToplistEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ToplistFragment() {
    val context = LocalContext.current
    val toplistEntries = remember { mutableStateListOf<ToplistEntry>() }

    // Load data
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val toplistDao = ToplistDatabase.getInstance(context).toplistDao()
            val entries = toplistDao.queryAll()
            toplistEntries.addAll(entries)
        }
    }

    // Main layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)) // Light sky-blue background
            .padding(16.dp)
    ) {
        Text(
            "Currently in the lead",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn {
            items(toplistEntries) { entry ->
                val index = toplistEntries.indexOf(entry)
                val trophyIcon = when (index) {
                    0 -> R.drawable.ic_trophy to Color(0xFFFFD700) // Gold
                    1 -> R.drawable.ic_trophy to Color(0xFFC0C0C0) // Silver
                    2 -> R.drawable.ic_trophy to Color(0xFFCD7F32) // Bronze
                    else -> null
                }

                // Toplist card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (trophyIcon != null) {
                            Icon(
                                painter = painterResource(id = trophyIcon.first),
                                contentDescription = null,
                                tint = trophyIcon.second,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp)
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = entry.username,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${entry.points} points",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}
