package hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.taskeditors

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

@Composable
fun EditTextPromptTaskComposable(
    latLng: LatLng,
    question: String,
    answer: String,
    onQuestionChange: (String) -> Unit,
    onAnswerChange: (String) -> Unit,
){
    //val question = remember { mutableStateOf("") }
    //val answer = remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
        Text("Specify a question for the players", modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = question,
            onValueChange = {
                onQuestionChange(it)
                            },
            label = { Text("Question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Text("Give the correct answer to your question", modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
        OutlinedTextField(
            value = answer,
            onValueChange = {
                onAnswerChange(it)
                            },
            label = { Text("Answer") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}