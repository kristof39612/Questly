package hu.bme.aut.szoftverarch.questly.fragments.main

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.style.TextAlign

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