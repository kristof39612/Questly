package hu.bme.aut.szoftverarch.questly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val activity = context as? Activity
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.placeholder_cat),
                contentDescription = "Login Placeholder Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(300.dp, 200.dp)
                    .background(Color.Gray, shape = RectangleShape)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) {
                        painterResource(id = R.drawable.ic_visible)
                    } else {
                        painterResource(id = R.drawable.ic_hidden)
                    }

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = icon, contentDescription = null)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    Intent(context, MainActivity::class.java).also {
                        startActivity(context, it, null)
                    }
                    activity?.finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    (activity as? LoginActivity)?.navigateToRegister()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Register")
            }
        }
    }
}