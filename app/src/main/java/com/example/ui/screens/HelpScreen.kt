package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TechCyanLight
import com.example.ui.theme.TechCyanPrimary
import com.example.ui.theme.TechEmerald
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HelpScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
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
                    text = "Help & Documentation",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Guidelines, WebView limits, and best practices",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                HelpTopicCard(
                    title = "1. Website URL & Security",
                    icon = Icons.Default.Security,
                    iconTint = TechEmerald,
                    content = """
                    • Always prioritize HTTPS over HTTP. Android blocks cleartext (HTTP) traffic by default to prevent data interception.
                    • If HTTP is used, Web2APK enables 'usesCleartextTraffic=true' in AndroidManifest.xml.
                    • Localhost, 127.0.0.1, or private intranet addresses are rejected by SSRF protection.
                    """.trimIndent()
                )
            }

            item {
                HelpTopicCard(
                    title = "2. Google Login & OAuth Policy",
                    icon = Icons.Default.Warning,
                    iconTint = TechAmber,
                    content = """
                    • Google restricts OAuth 2.0 authentication inside embedded WebViews for security reasons ('disallowed_useragent' error).
                    • Recommended solution: In 'User Agent & Link Routing', configure External Link Handling to open authentication providers in the external system browser (Chrome/Custom Tabs).
                    • The generated project template supports deep linking so OAuth redirects can return to your app seamlessly.
                    """.trimIndent()
                )
            }

            item {
                HelpTopicCard(
                    title = "3. Package Name & Versioning",
                    icon = Icons.Default.Info,
                    iconTint = TechCyanLight,
                    content = """
                    • Package names must contain at least two segments separated by dots (e.g. com.company.myapp).
                    • Only lowercase alphanumeric characters and underscores are permitted.
                    • Version Code is a monotonically increasing integer (e.g. 1, 2, 3) required by Google Play for updates.
                    • Version Name is the user-facing string (e.g. 1.0.0).
                    """.trimIndent()
                )
            }

            item {
                HelpTopicCard(
                    title = "4. Permissions & Hardware Access",
                    icon = Icons.Default.Lock,
                    iconTint = TechCyanPrimary,
                    content = """
                    • Camera & Microphone: Used for WebRTC audio/video calls or photo/receipt capture. Handled via Android ActivityResultContracts and WebChromeClient.onPermissionRequest.
                    • Geolocation: Handled via HTML5 navigator.geolocation and ACCESS_FINE_LOCATION.
                    • Download Manager: Downloads are enqueued to the native Android DownloadManager and saved directly to the public Downloads folder.
                    """.trimIndent()
                )
            }

            item {
                HelpTopicCard(
                    title = "5. Compilation & APK Signing",
                    icon = Icons.Default.HelpOutline,
                    iconTint = MaterialTheme.colorScheme.primary,
                    content = """
                    • Debug APK: Signed automatically with standard Android debug keystore. Perfect for direct side-loading and testing on any device.
                    • Release APK: Requires a private keystore. Web2APK never saves passwords permanently in plain text for maximum security.
                    • Local Build: You can always export the full project source as a .ZIP file and run './gradlew assembleRelease' in Android Studio or GitHub Actions.
                    """.trimIndent()
                )
            }
        }
    }
}

@Composable
fun HelpTopicCard(
    title: String,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    content: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
