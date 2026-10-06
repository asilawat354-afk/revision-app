package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyanLight
import com.example.ui.theme.TechCyanPrimary
import com.example.ui.theme.TechEmerald
import com.example.ui.theme.TechNavyCard
import com.example.ui.theme.TechRose
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BuildServerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val workerStatus by viewModel.workerStatus.collectAsStateWithLifecycle()
    val workerUrl by viewModel.workerUrl.collectAsStateWithLifecycle()
    val isChecking by viewModel.isCheckingWorker.collectAsStateWithLifecycle()

    var inputUrl by remember(workerUrl) { mutableStateOf(workerUrl) }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Dashboard) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "Build Server & Worker API",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Self-hosted Docker Android compilation engine",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (workerStatus.isConnected) TechEmerald.copy(alpha = 0.2f) else TechAmber.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (workerStatus.isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = if (workerStatus.isConnected) TechEmerald else TechAmber,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (workerStatus.isConnected) "Worker Server Connected" else "Worker Server Disconnected",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = if (workerStatus.isConnected) "Ready for direct remote Gradle compilation" else "Running in Standalone Project Mode",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (workerStatus.isConnected) TechEmerald else TechAmber
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (workerStatus.isConnected) {
                            Text(
                                text = "Supported Gradle: ${workerStatus.supportedGradleVersion} • SDK: ${workerStatus.supportedAndroidSdk}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                            )
                            Text(
                                text = "Active Jobs: ${workerStatus.activeJobsCount} / ${workerStatus.maxConcurrentJobs}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                            )
                        } else {
                            Text(
                                text = "When the worker is disconnected, Web2APK Builder operates in 'Project Synthesizer' mode: it generates the 100% complete standalone Android Studio Project and packages it as a .ZIP file for local compilation.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                            )
                        }
                    }
                }
            }

            // Connection Settings
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Worker Endpoint URL",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        OutlinedTextField(
                            value = inputUrl,
                            onValueChange = {
                                inputUrl = it
                                viewModel.updateWorkerUrl(it)
                            },
                            label = { Text("Build Worker Server Base URL") },
                            placeholder = { Text("http://10.0.2.2:8080 or https://build.yourdomain.com") },
                            leadingIcon = { Icon(imageVector = Icons.Default.Dns, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.updateWorkerUrl(inputUrl)
                                    viewModel.checkWorkerHealth()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isChecking,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (isChecking) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pinging...")
                                } else {
                                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Test Connection")
                                }
                            }
                        }

                        if (workerStatus.errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = TechRose.copy(alpha = 0.12f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = TechRose, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = workerStatus.errorMessage ?: "",
                                        style = MaterialTheme.typography.bodySmall.copy(color = TechRose)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Docker Setup Guide
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "How to run the Docker Build Worker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "To enable 1-click remote APK generation, run our pre-packaged Build Worker container on any VPS, cloud VM, or your local machine:",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        val dockerCmd = "docker run -d -p 8080:8080 \\\n  -e PORT=8080 \\\n  -v /var/run/web2apk/builds:/app/builds \\\n  ghcr.io/web2apk/build-worker:latest"

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("BASH COMMAND", style = MaterialTheme.typography.labelSmall.copy(color = TechCyanLight, fontWeight = FontWeight.Bold))
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(dockerCmd))
                                            Toast.makeText(context, "Command copied", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = TechCyanLight, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dockerCmd,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE2E8F0)
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Text(
                            text = "Standard Worker REST API Specs:",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        ApiEndpointItem("GET /api/health", "Health check, worker stats, supported Gradle version")
                        ApiEndpointItem("POST /api/builds", "Submits project configuration payload, returns buildId")
                        ApiEndpointItem("GET /api/builds/:id/status", "Polls stage progress, logs, and completion status")
                        ApiEndpointItem("GET /api/builds/:id/download", "Streams signed APK with application/vnd.android.package-archive")
                        ApiEndpointItem("POST /api/builds/:id/cancel", "Terminates ongoing Gradle execution & cleans temporary directory")
                    }
                }
            }
        }
    }
}

@Composable
fun ApiEndpointItem(endpoint: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = endpoint,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = TechCyanPrimary
            )
        )
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}
