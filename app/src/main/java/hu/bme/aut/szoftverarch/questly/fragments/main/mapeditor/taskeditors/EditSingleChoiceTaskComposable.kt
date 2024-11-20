package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

@Composable
fun EditSingleChoiceTaskComposable(
    latLng: LatLng
) {
    val answercount = remember { mutableIntStateOf(3) }
    var question by remember { mutableStateOf("") }
    var answers by remember { mutableStateOf(List(5) { "" }) }
    val context = LocalContext.current
    var correctAnswerIndex by remember { mutableIntStateOf(-1) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)) {
        Text(
            "Specify a question for the players",
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = question,
            onValueChange = {question = it},
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Text(
            "Possible answers",
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
        )
        Box(modifier = Modifier.fillMaxHeight()) { //0.4f ha googlemaps
            LazyColumn {
                items(answercount.intValue) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                    OutlinedTextField(
                        value = answers[index],
                        onValueChange = { newVal ->
                            answers = answers.toMutableList().apply{
                                this[index] = newVal
                            }
                        },
                        label = { Text("${index + 1}. Answer") },
                        modifier = Modifier.fillMaxWidth(0.90f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(
                        selected = (correctAnswerIndex == index),
                        onClick = { correctAnswerIndex = index }
                    )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = {
                            if (answercount.intValue <= 4) {
                                answercount.intValue += 1
                            } else {
                                Toast.makeText(
                                    context,
                                    "You can only have max 5 answers!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text("Add answer")
                        }
                        Button(onClick = {
                            if (answercount.intValue >= 3) {
                                answercount.intValue -= 1
                                answers = answers.toMutableList().apply{
                                    this[answercount.intValue] = ""
                                }
                                correctAnswerIndex = -1
                            } else {
                                Toast.makeText(
                                    context,
                                    "You need to have at least 2 answers!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Text("Remove answer")
                        }
                    }
                }
            }
        }

    }
}