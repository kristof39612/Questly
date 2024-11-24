package hu.bme.aut.szoftverarch.questly

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import hu.bme.aut.szoftverarch.questly.data.database.LogEntryDatabase
import hu.bme.aut.szoftverarch.questly.data.database.TaskPointDatabase
import hu.bme.aut.szoftverarch.questly.data.database.ToplistDatabase
import hu.bme.aut.szoftverarch.questly.data.networking.RetrofitInstance
import hu.bme.aut.szoftverarch.questly.data.utils.LatLong
import hu.bme.aut.szoftverarch.questly.graphics.LockScreenOrientation
import hu.bme.aut.szoftverarch.questly.fragments.main.HomeScreenFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.logentry.LogEntryDetailedViewFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.logentry.LogEntryListFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.ProfileScreen
import hu.bme.aut.szoftverarch.questly.fragments.main.SolveTaskScreen
import hu.bme.aut.szoftverarch.questly.fragments.main.ToplistFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.MapEditorFragment
import hu.bme.aut.szoftverarch.questly.fragments.main.mapeditor.TaskEditorFragment
import hu.bme.aut.szoftverarch.questly.graphics.LoadingDialog
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
    val taskPointDatabase = TaskPointDatabase.getInstance(context)
    val taskPointDao = taskPointDatabase.taskPointDao()
    val toplistDatabase = ToplistDatabase.getInstance(context)
    val toplistDao = toplistDatabase.toplistDao()
    val logentryDatabase = LogEntryDatabase.getInstance(context)
    val logentryDao = logentryDatabase.logEntryDao()
    val apiService = RetrofitInstance.getAuthorizedApi(context)
    var showProgress by remember { mutableStateOf(false) }
    var userPoints by remember { mutableIntStateOf(0) }
    var userName by remember { mutableStateOf("Anonymus") }
    val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)

    fun logmeout() {
        val sp = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
        activity.finish()
    }

    if (showProgress) {
        LoadingDialog()
    }

    fun refreshFromBackend() {
        showProgress = true
        scope.launch {
            try {
                val taskpointResp = apiService.getTaskPoints()
                if (taskpointResp.isSuccessful) {
                    taskPointDao.deleteAll()
                    for (taskPoint in taskpointResp.body()!!) {
                        taskPointDao.insertAll(taskPoint)
                    }
                }

                val toplistResp = apiService.getToplist()
                if (toplistResp.isSuccessful) {
                    toplistDao.deleteAll()
                    for (entry in toplistResp.body()!!) {
                        toplistDao.insertAll(entry)
                    }
                }

                val userpointResp = apiService.getUserPoints()
                if (userpointResp.isSuccessful) {
                    val points = userpointResp.body()?.points
                    userName = userpointResp.body()?.username ?: "Anonymus"
                    userPoints = points ?: 0
                }

                val ctaskResp = apiService.getCurrentTask()
                if (ctaskResp.isSuccessful) {
                    val task = ctaskResp.body()?.taskPointId
                    if (task != null) {
                        val editor = sharedPreferences.edit()
                        editor.putString("currentTask", task.toString())
                        editor.apply()
                    }
                } else {
                    sharedPreferences.edit().remove("currentTask").apply()
                }

                val logentryResp = apiService.getLogEntries()
                logentryDao.deleteAll()
                if (logentryResp.isSuccessful) {
                    val logEntries = logentryResp.body()
                    for (entry in logEntries!!) {
                        logentryDao.insertAll(entry)
                    }
                }
                val userIdResp = apiService.getUserId()
                if (userIdResp.isSuccessful) {
                    val userId = userIdResp.body()
                    val editor = sharedPreferences.edit()
                    if (userId != null) {
                        editor.putString("userID", userId.toString())
                    }
                    editor.apply()
                }
                val userRoleResp = apiService.getUserRole()
                if (userRoleResp.isSuccessful) {
                    val role = userRoleResp.body()
                    val editor = sharedPreferences.edit()
                    if (role != null) {
                        val crole = when (role) {
                            1L -> "ADMIN"
                            0L -> "USER"
                            else -> "USER"
                        }
                        editor.putString("userRole", crole)
                    }
                    editor.apply()
                }
                if(!userIdResp.isSuccessful || !userRoleResp.isSuccessful){
                    throw Exception("Session expired, please log in again")
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.errorLabel) + " ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                logmeout()
            } finally {
                showProgress = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshFromBackend()
    }

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
                    //val username = context.getSharedPreferences("UserData", 0).getString("userEmail", "placeholder")
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.usernameLabel) + " ")
                        }
                        append(userName)
                    }, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(R.string.pointLabel) + " ")
                        }
                        append(userPoints.toString())
                    }, style = MaterialTheme.typography.bodyLarge)
                    if (sharedPreferences.getString("userRole", "USER") == "ADMIN") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(stringResource(R.string.adminLabel))
                            }
                        }, style = MaterialTheme.typography.bodyLarge, color = Color.Red)

                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_handyman),
                        text = stringResource(R.string.editor),
                        onClick = {
                            navController.navigate("mapeditor") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                    //inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_menu_book),
                        text = stringResource(R.string.log),
                        onClick = {
                            navController.navigate("logentries") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                    //inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ModernButton(
                        icon = painterResource(id = R.drawable.ic_logout),
                        text = stringResource(R.string.logout),
                        onClick = {
                            logmeout()
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
                                Text(stringResource(R.string.app_name))
                            }
                            //Spacer(modifier = Modifier.weight(0.1f))
                            Column(modifier = Modifier.weight(0.13f)) {
                                IconButton(onClick = {
                                    refreshFromBackend()
                                }) {
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
                composable("profile") { ProfileScreen(navController) }
                composable("toplist") { ToplistFragment() }
                composable("solveTask/{taskPointId}") { backStackEntry ->
                    val taskPointId = backStackEntry.arguments?.getString("taskPointId")
                    taskPointId?.let {
                        SolveTaskScreen(navController = navController, taskId = it)
                    }
                }
                composable("logentries") { LogEntryListFragment(navController) }
                composable("logentry/{logEntryId}") { backStackEntry ->
                    val logEntryId = backStackEntry.arguments?.getString("logEntryId")
                    logEntryId?.let {
                        LogEntryDetailedViewFragment(navController = navController, logEntryId = it)
                    }
                }
                composable("mapeditor") { MapEditorFragment(navController) }
                composable("edit/taskpoint/{taskPointId}") { backStackEntry ->
                    val taskPointId = backStackEntry.arguments?.getString("taskPointId")
                    taskPointId?.let {
                        TaskEditorFragment(taskID = it, navController = navController)
                    }
                }
                composable("edit/newTaskpoint/{latlong}") { backStackEntry ->
                    val latlong = backStackEntry.arguments?.getString("latlong")
                    latlong?.let {
                        TaskEditorFragment(
                            location = LatLong.fromString(it),
                            navController = navController
                        )
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
            label = { Text(stringResource(R.string.homeMenu)) },
            selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
            onClick = {
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                        //inclusive = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_trophy),
                    contentDescription = stringResource(R.string.toplistMenu)
                )
            },
            label = { Text(stringResource(R.string.toplistMenu)) },
            selected = currentDestination?.hierarchy?.any { it.route == "toplist" } == true,
            onClick = {
                navController.navigate("toplist") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                        //inclusive = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.profileMenu)) },
            label = { Text(stringResource(R.string.profileMenu)) },
            selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
            onClick = {
                navController.navigate("profile") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                        //inclusive = true
                    }
                    launchSingleTop = true
                    restoreState = true
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
