package com.example.ui.screens

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BuildRecord
import com.example.data.model.BuildStatus
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyanLight
import com.example.ui.theme.TechCyanPrimary
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechNavyCard
import com.example.ui.theme.TechRose
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun BuildProgressScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val build by viewModel.activeBuild.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showLogs by remember { mutableStateOf(false) }

    val currentBuild = build

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Build Pipeline",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = currentBuild?.id ?: "Preparing...",
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyanPrimary)
                    )
                }
            }

            if (currentBuild?.status in listOf(BuildStatus.QUEUED, BuildStatus.PREPARING, BuildStatus.CONFIGURING, BuildStatus.RUNNING_GRADLE, BuildStatus.SIGNING)) {
                OutlinedButton(
                    onClick = { viewModel.cancelActiveBuild() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TechRose)
                ) {
                    Icon(imageVector = Icons.Default.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancel", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (currentBuild == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active build found.")
            }
            return
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
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
                                    text = currentBuild.appName,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = currentBuild.packageName,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TechCyanLight)
                                )
                            }

                            StatusBadge(currentBuild.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Indicator
                        LinearProgressIndicator(
                            progress = { currentBuild.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = when (currentBuild.status) {
                                BuildStatus.COMPLETED -> TechEmerald
                                BuildStatus.FAILED -> TechRose
                                else -> TechCyanPrimary
                            },
                            trackColor = Color(0xFF334155)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentBuild.stageMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFCBD5E1),
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${currentBuild.progressPercent}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TechCyanLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Real-Time Progress Stage Timeline
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Pipeline Stages",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        val isDone = currentBuild.status == BuildStatus.COMPLETED
                        val isFailed = currentBuild.status == BuildStatus.FAILED

                        StageRow("Preparing project workspace", isDone || currentBuild.progressPercent >= 20, currentBuild.progressPercent in 1..19)
                        StageRow("Copying templates & assets", isDone || currentBuild.progressPercent >= 35, currentBuild.progressPercent in 20..34)
                        StageRow("Configuring WebView engine", isDone || currentBuild.progressPercent >= 50, currentBuild.progressPercent in 35..49)
                        StageRow("Configuring manifest & permissions", isDone || currentBuild.progressPercent >= 65, currentBuild.progressPercent in 50..64)
                        StageRow("Running Gradle build & compile", isDone || currentBuild.progressPercent >= 80, currentBuild.progressPercent in 65..79)
                        StageRow("Signing APK (Keystore)", isDone || currentBuild.progressPercent >= 90, currentBuild.progressPercent in 80..89)
                        StageRow("Verifying APK package integrity", isDone, currentBuild.progressPercent in 90..99)
                        StageRow("Finalizing & export", isDone, false)
                    }
                }
            }

            // Action banner when completed / failed
            item {
                when (currentBuild.status) {
                    BuildStatus.COMPLETED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TechEmerald.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (currentBuild.isRealBuild) "APK Compiled Successfully! 🎉" else "Project Synthesized & Packaged! 🎉",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TechEmerald)
                                        )
                                        Text(
                                            text = "Package: ${currentBuild.packageName} • v${currentBuild.versionName}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                if (currentBuild.isRealBuild && currentBuild.apkDownloadUrl != null) {
                                    // REAL APK DOWNLOAD VIA WORKER
                                    Button(
                                        onClick = {
                                            try {
                                                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                                val req = DownloadManager.Request(Uri.parse(currentBuild.apkDownloadUrl))
                                                    .setTitle("${currentBuild.appName}.apk")
                                                    .setDescription("Downloading generated APK...")
                                                    .setMimeType("application/vnd.android.package-archive")
                                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${currentBuild.appName}.apk")
                                                dm.enqueue(req)
                                                Toast.makeText(context, "Download started: ${currentBuild.appName}.apk", Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechEmerald),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("download_apk_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Download, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Download APK (${currentBuild.apkFileSize / 1024} KB)")
                                    }
                                } else {
                                    // LOCAL SOURCE PROJECT ZIP EXPORT
                                    Button(
                                        onClick = {
                                            val zip = viewModel.exportProjectZip()
                                            if (zip != null) {
                                                viewModel.shareProjectZip(zip)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("download_source_project_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Export & Share Complete Android Project (.zip)")
                                    }
                                }
                            }
                        }
                    }

                    BuildStatus.FAILED -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TechRose.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = TechRose, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Build Pipeline Failed",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TechRose)
                                        )
                                        Text(
                                            text = currentBuild.errorMessage ?: "An error occurred during build pipeline execution.",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.triggerBuild() },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechRose),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retry Build")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.navigateTo(Screen.Builder) },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Edit Settings")
                                    }
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }

            // Expandable Build Logs viewer
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Build Pipeline Logs",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(currentBuild.logs))
                                        Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Logs", tint = TechCyanLight, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF020617))
                                .padding(12.dp)
                        ) {
                            LazyColumn {
                                item {
                                    Text(
                                        text = currentBuild.logs.ifBlank { "Logs will appear here as build executes..." },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            lineHeight = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StageRow(title: String, isDone: Boolean, isInProgress: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> TechEmerald.copy(alpha = 0.2f)
                        isInProgress -> TechCyanPrimary.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isDone -> Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = TechEmerald, modifier = Modifier.size(14.dp))
                isInProgress -> CircularProgressIndicator(color = TechCyanPrimary, strokeWidth = 2.dp, modifier = Modifier.size(12.dp))
                else -> Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isInProgress) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isDone -> MaterialTheme.colorScheme.onSurface
                    isInProgress -> TechCyanPrimary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        )
    }
}

@Composable
fun StatusBadge(status: BuildStatus) {
    val (color, text) = when (status) {
        BuildStatus.COMPLETED -> TechEmerald to "Completed"
        BuildStatus.FAILED -> TechRose to "Failed"
        BuildStatus.CANCELLED -> TechAmber to "Cancelled"
        BuildStatus.QUEUED -> Color(0xFF94A3B8) to "Queued"
        else -> TechCyanLight to "Building..."
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
