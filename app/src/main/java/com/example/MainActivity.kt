package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.BuildProgressScreen
import com.example.ui.screens.BuildServerScreen
import com.example.ui.screens.BuilderScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TechCyanPrimary
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Intelligent BackHandler navigation
    if (currentScreen !is Screen.Dashboard) {
        BackHandler {
            viewModel.navigateTo(Screen.Dashboard)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom navigation on primary screens
            if (currentScreen !is Screen.BuildProgress) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Dashboard,
                        onClick = { viewModel.navigateTo(Screen.Dashboard) },
                        icon = { Icon(imageVector = Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_dashboard")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Builder,
                        onClick = { viewModel.startNewProject() },
                        icon = { Icon(imageVector = Icons.Default.AddCircle, contentDescription = "New APK") },
                        label = { Text("New APK") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_builder")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.History,
                        onClick = { viewModel.navigateTo(Screen.History) },
                        icon = { Icon(imageVector = Icons.Default.History, contentDescription = "History") },
                        label = { Text("History") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_history")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Projects,
                        onClick = { viewModel.navigateTo(Screen.Projects) },
                        icon = { Icon(imageVector = Icons.Default.Folder, contentDescription = "Projects") },
                        label = { Text("Projects") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_projects")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.ServerConfig,
                        onClick = { viewModel.navigateTo(Screen.ServerConfig) },
                        icon = { Icon(imageVector = Icons.Default.Storage, contentDescription = "Server") },
                        label = { Text("Server") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanPrimary,
                            indicatorColor = TechCyanPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_server")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Dashboard -> DashboardScreen(viewModel = viewModel)
                is Screen.Builder -> BuilderScreen(viewModel = viewModel)
                is Screen.BuildProgress -> BuildProgressScreen(viewModel = viewModel)
                is Screen.History -> HistoryScreen(viewModel = viewModel)
                is Screen.Projects -> ProjectsScreen(viewModel = viewModel)
                is Screen.Templates -> TemplatesScreen(viewModel = viewModel)
                is Screen.ServerConfig -> BuildServerScreen(viewModel = viewModel)
                is Screen.Help -> HelpScreen(viewModel = viewModel)
            }
        }
    }
}
