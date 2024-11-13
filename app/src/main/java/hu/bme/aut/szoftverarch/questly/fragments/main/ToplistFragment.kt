package hu.bme.aut.szoftverarch.questly.fragments.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val toplistDao = ToplistDatabase.getInstance(context).toplistDao()
            val entries = toplistDao.queryAll()
            toplistEntries.addAll(entries)
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(
            "Currently in the lead",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth(),
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                        .padding(8.dp)
                ) {
                    if (trophyIcon != null) {
                        Icon(
                            painter = painterResource(id = trophyIcon.first),
                            contentDescription = null,
                            tint = trophyIcon.second,
                            modifier = Modifier.weight(0.1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                    Text(
                        text = entry.username,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.4f)
                    )
                    Text(
                        text = "${entry.points} points",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.5f)
                    )
                }
            }
        }
    }
}