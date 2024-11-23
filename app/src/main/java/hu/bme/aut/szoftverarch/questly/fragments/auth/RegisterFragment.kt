package hu.bme.aut.szoftverarch.questly.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.networking.RegisterRequest
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import kotlinx.coroutines.launch
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        return ComposeView(requireContext()).apply {
            setContent {
                RegisterScreen(onBackPressed = { callback.handleOnBackPressed() })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalEncodingApi::class)
@Composable
fun RegisterScreen(onBackPressed: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val apiService = RetrofitInstance.api
    var showProgress by remember { mutableStateOf(false) }

    var emailTextFieldColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(email) {
        emailTextFieldColor =
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            ) {
                Color.Red
            } else {
                Color.Transparent
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF87CEEB)),
                title = { Text(stringResource(R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            color = Color(0xFF87CEEB),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(paddingValues),
        ) {

            if (showProgress) {
                LoadingDialog(stringResource(R.string.registering))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.Email)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .border(2.dp, emailTextFieldColor)
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.Password)) },
                    singleLine = true,
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
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.ConfirmPassword)) },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                            if (password.length >= 8) {
                                if (password == confirmPassword && emailTextFieldColor == Color.Transparent) {
                                    showProgress = true
                                    scope.launch {
                                        try {
                                            val magickeyinbase64 = "pQjbshoJGc3EAkRaa5FXsA=="
                                            val magickey = Base64.decode(magickeyinbase64)
                                            val ivtxt: ByteArray = context.resources.openRawResource(R.raw.iv)
                                                .use { input ->
                                                    input.readBytes()
                                                }
                                            val secretkey = SecretKeySpec(magickey, "AES")
                                            val cypher =
                                                javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding")
                                            cypher.init(
                                                javax.crypto.Cipher.ENCRYPT_MODE,
                                                secretkey,
                                                javax.crypto.spec.IvParameterSpec(ivtxt))
                                            val encrypted = cypher.doFinal(password.toByteArray())
                                            val rr = RegisterRequest(
                                                email = email,
                                                password = Base64.encode(encrypted),
                                                username = username
                                            )
                                            val serverResponse = apiService.register(rr)
                                            if (serverResponse.isSuccessful) {
                                                if (serverResponse.body()!!.token != null) {
                                                    Toast.makeText(
                                                        context,
                                                        R.string.RegisterSuccess,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    serverResponse.body()!!.errorMessage.let {
                                                        Toast.makeText(
                                                            context,
                                                            it,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    R.string.RegisterFailed,
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
                                } else {
                                    Toast.makeText(
                                        context,
                                        R.string.PasswordsDoNotMatch,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.PasswordTooShort,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                R.string.fillOutAllFields,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Backend " + stringResource(R.string.register))
                }

            }
        }
    }
}