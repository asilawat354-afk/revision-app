package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BuildRecord
import com.example.data.model.BuildStatus
import com.example.data.model.WebProject
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyanLight
import com.example.ui.theme.TechCyanPrimary
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechIndigo
import com.example.ui.theme.TechNavyCard
import com.example.ui.theme.TechRose
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val totalBuilds by viewModel.totalBuildsCount.collectAsStateWithLifecycle()
    val successBuilds by viewModel.successfulBuildsCount.collectAsStateWithLifecycle()
    val failedBuilds by viewModel.failedBuildsCount.collectAsStateWithLifecycle()
    val projectCount by viewModel.totalProjectsCount.collectAsStateWithLifecycle()
    val workerStatus by viewModel.workerStatus.collectAsStateWithLifecycle()
    val recentBuilds by viewModel.builds.collectAsStateWithLifecycle()
    val projects by viewModel.projects.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechNavyCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Web2APK Builder",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Production Android APK Generator Pipeline",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TechCyanLight)
                            )
                        }

                        // Worker connection pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (workerStatus.isConnected) TechEmerald.copy(alpha = 0.15f) else TechAmber.copy(alpha = 0.15f),
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.ServerConfig) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (workerStatus.isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                    contentDescription = "Build Server Status",
                                    tint = if (workerStatus.isConnected) TechEmerald else TechAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (workerStatus.isConnected) "Worker Online" else "Local Mode",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (workerStatus.isConnected) TechEmerald else TechAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Action Button
                    Button(
                        onClick = { viewModel.startNewProject() },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("create_new_apk_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create APK",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Create New APK",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Quick navigation shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Templates",
                    subtitle = "4 Ready Presets",
                    icon = Icons.Default.Layers,
                    color = TechIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.Templates) }
                )
                QuickNavCard(
                    title = "Build Server",
                    subtitle = if (workerStatus.isConnected) "Connected" else "Setup Docker",
                    icon = Icons.Default.Storage,
                    color = TechCyanLight,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.ServerConfig) }
                )
                QuickNavCard(
                    title = "Help & Docs",
                    subtitle = "Guidelines",
                    icon = Icons.Default.HelpOutline,
                    color = TechEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(Screen.Help) }
                )
            }
        }

        // Stats grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Build Pipeline Overview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Builds",
                        value = totalBuilds.toString(),
                        icon = Icons.Default.Build,
                        accentColor = TechCyanLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Successful",
                        value = successBuilds.toString(),
                        icon = Icons.Default.CheckCircle,
                        accentColor = TechEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Failed",
                        value = failedBuilds.toString(),
                        icon = Icons.Default.Error,
                        accentColor = TechRose,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Projects",
                        value = projectCount.toString(),
                        icon = Icons.Default.Folder,
                        accentColor = TechIndigo,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Currently Running or Recent Build
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Builds",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                if (recentBuilds.isNotEmpty()) {
                    Text(
                        text = "View All (${recentBuilds.size})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TechCyanPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.History) }
                    )
                }
            }
        }

        if (recentBuilds.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📦", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Builds Yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter any website URL to configure and build your first Android APK.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.startNewProject() },
                            colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary)
                        ) {
                            Text("Create First App")
                        }
                    }
                }
            }
        } else {
            items(recentBuilds.take(3)) { build ->
                BuildItemCard(
                    build = build,
                    onView = { viewModel.rebuild(build) },
                    onRebuild = { viewModel.rebuild(build) }
                )
            }
        }

        // Saved Projects Section
        if (projects.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Projects",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Manage (${projects.size})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TechCyanPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Projects) }
                    )
                }
            }

            items(projects.take(2)) { project ->
                ProjectSummaryCard(
                    project = project,
                    onEdit = { viewModel.loadProject(project) }
                )
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
fun BuildItemCard(
    build: BuildRecord,
    onView: () -> Unit,
    onRebuild: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(build.createdAt))
    val statusColor = when (build.status) {
        BuildStatus.COMPLETED -> TechEmerald
        BuildStatus.FAILED -> TechRose
        BuildStatus.CANCELLED -> TechAmber
        else -> TechCyanLight
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = build.appName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = build.packageName,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = build.status.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "v${build.versionName} • $dateStr",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                if (build.apkFileSize > 0) {
                    Text(
                        text = "${build.apkFileSize / 1024} KB",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TechCyanPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectSummaryCard(
    project: WebProject,
    onEdit: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = project.url,
                    style = MaterialTheme.typography.bodySmall.copy(color = TechCyanPrimary),
                    maxLines = 1
                )
                Text(
                    text = project.packageName,
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Configure", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
