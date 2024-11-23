package hu.bme.aut.szoftverarch.questly.fragments.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hu.bme.aut.szoftverarch.questly.LoginActivity
import hu.bme.aut.szoftverarch.questly.MainActivity
import hu.bme.aut.szoftverarch.questly.R
import hu.bme.aut.szoftverarch.questly.data.networking.LoginRequest
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
import kotlinx.coroutines.launch
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                VideoBackgroundLoginScreen()
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoBackgroundLoginScreen(
) {
    // Set up ExoPlayer
    val context = LocalContext.current
    val videoUri: Uri = Uri.parse("android.resource://${context.packageName}/${R.raw.clouds}")
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
        }
    )
    DisposableEffect(
        Unit
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Overlay Login UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginScreen()
    }
}

@kotlin.OptIn(ExperimentalEncodingApi::class)
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
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent)
    systemUiController.isSystemBarsVisible = true

    if (sharedPreferences.getString("userToken", null) != null) {
        context.startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (showProgress) {
                LoadingDialog(stringResource(R.string.loggingin))
            }
            val infiniteTransition = rememberInfiniteTransition(label = "")
            Text(
                text = "Questly",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )
            // Diagonal offset animations for horizontal and vertical movement
            val offsetX by infiniteTransition.animateFloat(
                initialValue = -30f,  // Slightly offset left
                targetValue = 30f,    // Slightly offset right
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            val offsetY by infiniteTransition.animateFloat(
                initialValue = 30f,   // Slightly offset down
                targetValue = -30f,   // Slightly offset up
                animationSpec = infiniteRepeatable(
                    animation = tween(6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            // Alpha animation for fading effect
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,  // Start with half opacity
                targetValue = 1f,     // Fade to full opacity
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )

            AnimatedContent(
                targetState = painterResource(id = R.drawable.map_background),
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = ""
            ) { targetImage ->
                // Container box with clipping to restrict the image movement within bounds
                Box(
                    modifier = Modifier
                        .size(300.dp, 200.dp)
                        .clip(RoundedCornerShape(16.dp))  // Rounded corners
                        .clipToBounds()   // Ensures the image stays within the visible boundary
                ) {
                    Image(
                        painter = targetImage,
                        contentDescription = "Login back Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                translationX = offsetX,   // Diagonal movement on X
                                translationY = offsetY,   // Diagonal movement on Y
                                alpha = alpha             // Fading effect
                            )
                    )
                }
            }

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
                    .clip(RoundedCornerShape(8.dp))
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
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
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
                                val lr = LoginRequest(email, Base64.encode(encrypted))
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x80FF8C00) // Semi-transparent orange
                ),
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x80FF8C00) // Semi-transparent orange
                ),
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