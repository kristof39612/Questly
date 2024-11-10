package hu.bme.aut.szoftverarch.questly

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.szoftverarch.questly.fragments.animation.LockScreenOrientation
import hu.bme.aut.szoftverarch.questly.fragments.main.HomeScreenFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.ProfileScreen
import hu.bme.aut.szoftverarch.questly.fragments.main.SettingsScreen
import hu.bme.aut.szoftverarch.questly.fragments.main.SolveTaskScreen
import hu.bme.aut.szoftverarch.questly.fragments.main.ToplistFragment
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
        //Main Content
    setContent {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        MainScreen(drawerState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerState.isOpen) {
                    scope.launch { drawerState.close() }
                } else {
                    finish()
                }
            }
        })
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(drawerState: DrawerState) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val activity = context as MainActivity
    //Database test
    //val taskPointDatabase = TaskPointDatabase.getInstance(context)
    //val taskPointDao = taskPointDatabase.taskPointDao()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.45f)
                    .background(Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    val username = context.getSharedPreferences("UserData", 0).getString("userEmail", "placeholder")
                    Text("Username: $username", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Points: placeholder", style = MaterialTheme.typography.bodyLarge)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_handyman),
                        text = "Editor",
                        onClick = { /* Handle Map Editor click */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_menu_book),
                        text = "Log",
                        onClick = { /* Handle Log click */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_menu_gallery),
                        text = "Menu1",
                        onClick = { /* Handle Menu1 click */ }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_menu_gallery),
                        text = "Menu2",
                        onClick = { /* Handle Menu2 click */ }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_logout),
                        text = "Logout",
                        onClick = {
                            val sp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            val editor = sp.edit()
                            editor.clear()
                            editor.apply()
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            activity.finish()
                        },
                        buttonColor = Color.Red,
                        textColor = Color.White
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(
                                modifier = Modifier.weight(0.80f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Questly")
                            }
                            //Spacer(modifier = Modifier.weight(0.1f))
                            Column(modifier = Modifier.weight(0.13f)) {
                                IconButton(onClick = {      //TODO: Refresh operations
                                }){
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                                }
                            }
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }

                )
            },
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreenFragment(navController) }
                composable("settings") { SettingsScreen() }
                composable("profile") { ProfileScreen() }
                composable("toplist") { ToplistFragment() }
                composable("solveTask/{taskPointId}") { backStackEntry ->
                    val taskPointId = backStackEntry.arguments?.getString("taskPointId")
                    taskPointId?.let {
                        SolveTaskScreen(navController = navController, taskId = it)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernButton(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.size(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, contentDescription = text, tint = textColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text, style = MaterialTheme.typography.bodySmall.copy(color = textColor))
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_trophy), contentDescription = "Toplist") },
            label = { Text("Toplist") },
            selected = currentDestination?.hierarchy?.any { it.route == "toplist" } == true,
            onClick = {
                navController.navigate("toplist") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
            onClick = {
                navController.navigate("settings") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainScreen(drawerState = DrawerState(DrawerValue.Closed))
}
