package hu.bme.aut.szoftverarch.questly.fragments.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import hu.bme.aut.szoftverarch.questly.LoginActivity
import hu.bme.aut.szoftverarch.questly.MainActivity
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.networking.LoginRequest
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()
    val apiService = RetrofitInstance.api
    val sharedPreferences = context.getSharedPreferences("UserData",Context.MODE_PRIVATE)
    var showProgress by remember { mutableStateOf(false) }

    if (sharedPreferences.getString("userToken", null) != null) {
        context.startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

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
                            Text(stringResource(R.string.loggingin), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

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
                label = { Text(stringResource(R.string.Email)) },
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
                label = { Text(stringResource(R.string.Password)) },
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
                    context.startActivity(Intent(context, MainActivity::class.java))
                    /*Intent(context, MainActivity::class.java).also {
                        startActivity(context, it, null)
                    }*/
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
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        showProgress = true
                        val lr = LoginRequest(email, password)
                        scope.launch {
                            try {
                                val call = apiService.login(lr)
                                if (call.isSuccessful) {
                                    val token = call.body()?.token
                                    val editor = sharedPreferences.edit()
                                    editor.putString("userToken", token)
                                    editor.putString("userEmail", email)
                                    editor.apply()
                                    // Save token
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                    activity?.finish()
                                } else {
                                    Toast.makeText(context, R.string.LoginFailed, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    R.string.backendUnavailable,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                showProgress = false
                            }

                        }
                    } else
                        Toast.makeText(context, R.string.fillOutAllFields, Toast.LENGTH_SHORT)
                            .show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Backend " + stringResource(R.string.login))
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
                Text(stringResource(R.string.register))
            }

        }
    }
}