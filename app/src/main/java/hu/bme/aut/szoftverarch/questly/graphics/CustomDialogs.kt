package hu.bme.aut.szoftverarch.questly.graphics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TaskCompletionDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Confirm Task completion") },
        text = { Text("Are you sure you want to complete this task?",textAlign = TextAlign.Center) },
        confirmButton = {
            Button(onClick = {
                onConfirm()
            }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("No")
            }
        }
    )
}

@Composable
fun ConfirmExitDialog(showDialog: MutableState<Boolean>, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Confirm Exit") },
            text = { Text("Are you sure you want to leave this task?") },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onConfirm()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false
                    onDismiss()
                }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun LoadingDialog(
    txt: String = "Waiting..."
) {
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
                Text(txt, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}