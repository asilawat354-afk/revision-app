package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun BuilderScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.currentProject.collectAsStateWithLifecycle()
    val urlValidation by viewModel.urlValidationResult.collectAsStateWithLifecycle()
    val isValidating by viewModel.isValidatingUrl.collectAsStateWithLifecycle()
    val workerStatus by viewModel.workerStatus.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var keyPassInput by remember { mutableStateOf("") }
    var storePassInput by remember { mutableStateOf("") }
    var showHttpWarningDialog by remember { mutableStateOf(false) }

    // Package Name Validator regex
    val packageRegex = remember { Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$") }
    val isPackageValid = remember(project.packageName) { packageRegex.matches(project.packageName) }

    val tabs = listOf("General", "WebView", "Permissions", "Build & Sign")

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
                        text = "Configure APK",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = project.name.ifBlank { "Untitled Project" },
                        style = MaterialTheme.typography.bodySmall.copy(color = TechCyanPrimary)
                    )
                }
            }

            Row {
                IconButton(onClick = { viewModel.saveCurrentProject() }) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save Project", tint = TechCyanPrimary)
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = TechCyanPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: GENERAL & IDENTITY
                    item {
                        SectionHeader("1. Website URL", "Target URL that the Android WebView will display")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = project.url,
                                    onValueChange = { viewModel.updateProject { p -> p.copy(url = it) } },
                                    label = { Text("Website URL (HTTPS preferred)") },
                                    placeholder = { Text("https://example.com") },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.Language, contentDescription = "URL")
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("website_url_input"),
                                    isError = project.url.startsWith("http://")
                                )

                                if (project.url.startsWith("http://")) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = TechAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "HTTP detected. Cleartext traffic will be enabled with security warning.",
                                            style = MaterialTheme.typography.labelSmall.copy(color = TechAmber)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.validateCurrentUrl() },
                                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("validate_website_button"),
                                    enabled = !isValidating && project.url.isNotBlank()
                                ) {
                                    if (isValidating) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Validating URL & SSRF Safety...")
                                    } else {
                                        Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Validate Website")
                                    }
                                }

                                // Validation result box
                                urlValidation?.let { res ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (res.isValid) TechEmerald.copy(alpha = 0.12f) else TechRose.copy(alpha = 0.12f)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                                                    contentDescription = null,
                                                    tint = if (res.isValid) TechEmerald else TechRose,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (res.isValid) "Website Valid & Reachable" else "Validation Failed",
                                                    style = MaterialTheme.typography.titleSmall.copy(
                                                        color = if (res.isValid) TechEmerald else TechRose,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }

                                            if (res.title != null) {
                                                Text(
                                                    text = "Page Title: ${res.title}",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                                )
                                            }

                                            Text(
                                                text = "HTTP ${res.httpCode} • Latency: ${res.responseTimeMs}ms • SSRF Check: ${if (res.isSsrfSafe) "Passed" else "Blocked"}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            )

                                            if (res.errorMessage != null) {
                                                Text(
                                                    text = res.errorMessage,
                                                    style = MaterialTheme.typography.bodySmall.copy(color = TechRose)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        SectionHeader("2. App Identity", "Package credentials and launcher identity")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = project.name,
                                    onValueChange = { viewModel.updateProject { p -> p.copy(name = it) } },
                                    label = { Text("App Name (Title)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("app_name_input")
                                )

                                OutlinedTextField(
                                    value = project.packageName,
                                    onValueChange = { viewModel.updateProject { p -> p.copy(packageName = it.trim().lowercase()) } },
                                    label = { Text("Package Name (e.g. com.company.app)") },
                                    singleLine = true,
                                    isError = !isPackageValid,
                                    supportingText = {
                                        if (!isPackageValid) {
                                            Text("Invalid format. Must follow com.domain.app (lowercase, dots, no spaces)", color = TechRose)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("package_name_input")
                                )

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = project.versionName,
                                        onValueChange = { viewModel.updateProject { p -> p.copy(versionName = it) } },
                                        label = { Text("Version Name") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = project.versionCode.toString(),
                                        onValueChange = {
                                            val code = it.toIntOrNull() ?: project.versionCode
                                            viewModel.updateProject { p -> p.copy(versionCode = code) }
                                        },
                                        label = { Text("Version Code") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = project.author,
                                    onValueChange = { viewModel.updateProject { p -> p.copy(author = it) } },
                                    label = { Text("Author / Organization") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    item {
                        SectionHeader("3. Launcher Icon & Splash Screen", "Visual customization")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Launcher Icon Preset", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconPresetPill(
                                        label = "Cyan Shield",
                                        emoji = "🛡️",
                                        isSelected = project.iconStyle == "CYAN_SHIELD",
                                        onClick = { viewModel.updateProject { it.copy(iconStyle = "CYAN_SHIELD") } }
                                    )
                                    IconPresetPill(
                                        label = "Globe",
                                        emoji = "🌐",
                                        isSelected = project.iconStyle == "BLUE_GLOBE",
                                        onClick = { viewModel.updateProject { it.copy(iconStyle = "BLUE_GLOBE") } }
                                    )
                                    IconPresetPill(
                                        label = "Rocket",
                                        emoji = "🚀",
                                        isSelected = project.iconStyle == "PURPLE_ROCKET",
                                        onClick = { viewModel.updateProject { it.copy(iconStyle = "PURPLE_ROCKET") } }
                                    )
                                    IconPresetPill(
                                        label = "Cart",
                                        emoji = "🛍️",
                                        isSelected = project.iconStyle == "EMERALD_CART",
                                        onClick = { viewModel.updateProject { it.copy(iconStyle = "EMERALD_CART") } }
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Splash Screen", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text("Android 12+ Core Splash Screen API", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                                    }
                                    Switch(
                                        checked = project.splashEnabled,
                                        onCheckedChange = { viewModel.updateProject { p -> p.copy(splashEnabled = it) } },
                                        colors = SwitchDefaults.colors(checkedThumbColor = TechCyanPrimary)
                                    )
                                }

                                if (project.splashEnabled) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(
                                            value = project.splashBgColor,
                                            onValueChange = { viewModel.updateProject { p -> p.copy(splashBgColor = it) } },
                                            label = { Text("Background Hex") },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = project.splashDurationMs.toString(),
                                            onValueChange = {
                                                val dur = it.toIntOrNull() ?: 1500
                                                viewModel.updateProject { p -> p.copy(splashDurationMs = dur) }
                                            },
                                            label = { Text("Duration (ms)") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: WEBVIEW ENGINE SETTINGS
                    item {
                        SectionHeader("WebView Engine Configuration", "Engine flags, caching, cookies, and scripts")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SettingSwitchItem(
                                    title = "Enable JavaScript",
                                    description = "Mandatory for modern dynamic single-page web applications.",
                                    checked = project.webViewConfig.enableJavaScript,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableJavaScript = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Enable DOM Storage",
                                    description = "Allows websites to use localStorage and sessionStorage.",
                                    checked = project.webViewConfig.enableDomStorage,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableDomStorage = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Enable Database Storage",
                                    description = "Allows IndexedDB and local web databases.",
                                    checked = project.webViewConfig.enableDatabase,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableDatabase = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Enable Cookies",
                                    description = "Keeps user logged in via HTTP and session cookies.",
                                    checked = project.webViewConfig.enableCookies,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableCookies = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Third-Party Cookies",
                                    description = "Required if your site embeds external payment/auth widgets.",
                                    checked = project.webViewConfig.enableThirdPartyCookies,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableThirdPartyCookies = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Pull to Refresh",
                                    description = "User can swipe down to reload the current webpage.",
                                    checked = project.webViewConfig.enablePullToRefresh,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enablePullToRefresh = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "File Download Manager",
                                    description = "Interceps download links and saves files to Android Downloads folder.",
                                    checked = project.webViewConfig.enableDownloadManager,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableDownloadManager = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "File & Camera Uploads",
                                    description = "Enables <input type='file'> picker for photos, docs, camera.",
                                    checked = project.webViewConfig.enableFileUpload,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(enableFileUpload = it)) } }
                                )
                            }
                        }
                    }

                    item {
                        SectionHeader("User Agent & Link Routing", "Browsing behavior and external app handoff")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("User Agent Mode", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                                Column {
                                    UaOptionItem(
                                        title = "Default Android WebView",
                                        selected = project.webViewConfig.userAgentMode == "DEFAULT",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(userAgentMode = "DEFAULT")) } }
                                    )
                                    UaOptionItem(
                                        title = "Mobile Chrome (Masquerade as full browser)",
                                        selected = project.webViewConfig.userAgentMode == "MOBILE_CHROME",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(userAgentMode = "MOBILE_CHROME")) } }
                                    )
                                    UaOptionItem(
                                        title = "Desktop Chrome",
                                        selected = project.webViewConfig.userAgentMode == "DESKTOP",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(userAgentMode = "DESKTOP")) } }
                                    )
                                    UaOptionItem(
                                        title = "Custom User Agent",
                                        selected = project.webViewConfig.userAgentMode == "CUSTOM",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(userAgentMode = "CUSTOM")) } }
                                    )
                                }

                                if (project.webViewConfig.userAgentMode == "CUSTOM") {
                                    OutlinedTextField(
                                        value = project.webViewConfig.customUserAgent,
                                        onValueChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(customUserAgent = it)) } },
                                        label = { Text("Custom User Agent String") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                Text("External Link Handling", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                                Column {
                                    UaOptionItem(
                                        title = "Open external links in system browser (Recommended)",
                                        selected = project.webViewConfig.linkRoutingMode == "EXTERNAL_BROWSER",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(linkRoutingMode = "EXTERNAL_BROWSER")) } }
                                    )
                                    UaOptionItem(
                                        title = "Keep all links inside WebView",
                                        selected = project.webViewConfig.linkRoutingMode == "ALL_INTERNAL",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(linkRoutingMode = "ALL_INTERNAL")) } }
                                    )
                                    UaOptionItem(
                                        title = "Whitelist specific domains only",
                                        selected = project.webViewConfig.linkRoutingMode == "WHITELIST_ONLY",
                                        onClick = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(linkRoutingMode = "WHITELIST_ONLY")) } }
                                    )
                                }

                                if (project.webViewConfig.linkRoutingMode == "WHITELIST_ONLY") {
                                    OutlinedTextField(
                                        value = project.webViewConfig.allowedDomains,
                                        onValueChange = { viewModel.updateProject { p -> p.copy(webViewConfig = p.webViewConfig.copy(allowedDomains = it)) } },
                                        label = { Text("Comma-separated domains (e.g. example.com, cdn.example.com)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: PERMISSIONS MATRIX
                    item {
                        SectionHeader("Android System Permissions", "Only request permissions strictly required by website features")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                SettingSwitchItem(
                                    title = "Internet (Required)",
                                    description = "INTERNET & ACCESS_NETWORK_STATE to fetch web pages.",
                                    checked = project.permissionsConfig.internet,
                                    enabled = false, // Always required
                                    onCheckedChange = {}
                                )

                                SettingSwitchItem(
                                    title = "Camera Support",
                                    description = "Enable if website uses barcode scanner or video recording (WebRTC / file capture).",
                                    checked = project.permissionsConfig.camera,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(permissionsConfig = p.permissionsConfig.copy(camera = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Microphone Support",
                                    description = "Enable if website performs voice search, audio chat or WebRTC calls.",
                                    checked = project.permissionsConfig.microphone,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(permissionsConfig = p.permissionsConfig.copy(microphone = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Location (Geolocation)",
                                    description = "Enables HTML5 navigator.geolocation (ACCESS_FINE_LOCATION).",
                                    checked = project.permissionsConfig.location,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(permissionsConfig = p.permissionsConfig.copy(location = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Notifications",
                                    description = "Allows website Web Push notifications via POST_NOTIFICATIONS (Android 13+).",
                                    checked = project.permissionsConfig.notifications,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(permissionsConfig = p.permissionsConfig.copy(notifications = it)) } }
                                )

                                SettingSwitchItem(
                                    title = "Downloads / Storage",
                                    description = "Allows saving downloaded files into Android public Downloads folder.",
                                    checked = project.permissionsConfig.storageDownloads,
                                    onCheckedChange = { viewModel.updateProject { p -> p.copy(permissionsConfig = p.permissionsConfig.copy(storageDownloads = it)) } }
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: SIGNING & BUILD PIPELINE
                    item {
                        SectionHeader("Signing Keystore Configuration", "Debug testing vs Production release signing")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    BuildTypePill(
                                        title = "Debug APK",
                                        subtitle = "Instant testing (Default debug keystore)",
                                        isSelected = !project.signingConfig.isRelease,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.updateProject { p -> p.copy(signingConfig = p.signingConfig.copy(isRelease = false)) } }
                                    )
                                    BuildTypePill(
                                        title = "Release APK",
                                        subtitle = "Production release signing",
                                        isSelected = project.signingConfig.isRelease,
                                        modifier = Modifier.weight(1f),
                                        onClick = { viewModel.updateProject { p -> p.copy(signingConfig = p.signingConfig.copy(isRelease = true)) } }
                                    )
                                }

                                if (project.signingConfig.isRelease) {
                                    OutlinedTextField(
                                        value = project.signingConfig.keyAlias,
                                        onValueChange = { viewModel.updateProject { p -> p.copy(signingConfig = p.signingConfig.copy(keyAlias = it)) } },
                                        label = { Text("Key Alias") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = keyPassInput,
                                        onValueChange = { keyPassInput = it },
                                        label = { Text("Key Password (Transient)") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        supportingText = { Text("Never saved in plaintext logs or database for security.") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = storePassInput,
                                        onValueChange = { storePassInput = it },
                                        label = { Text("Store Password (Transient)") },
                                        visualTransformation = PasswordVisualTransformation(),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    item {
                        SectionHeader("Build Summary & Trigger", "Verify configuration before launching compilation")
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TechNavyCard),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pipeline Target:", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)))
                                    Text(
                                        text = if (project.signingConfig.isRelease) "Signed Release APK" else "Debug APK",
                                        style = MaterialTheme.typography.titleSmall.copy(color = TechCyanLight, fontWeight = FontWeight.Bold)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Target URL:", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)))
                                    Text(
                                        text = project.url.take(30),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Package:", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)))
                                    Text(
                                        text = project.packageName,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Build Worker:", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF94A3B8)))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (workerStatus.isConnected) TechEmerald else TechAmber)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (workerStatus.isConnected) "Connected (${workerStatus.supportedGradleVersion})" else "Local Synthesis Mode",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = if (workerStatus.isConnected) TechEmerald else TechAmber,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // PRIMARY BUILD BUTTON
                                Button(
                                    onClick = {
                                        if (project.url.startsWith("http://")) {
                                            showHttpWarningDialog = true
                                        } else {
                                            viewModel.triggerBuild(keyPassInput, storePassInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TechCyanPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = isPackageValid && project.url.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("build_apk_button")
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "BUILD APK",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }

                                // EXPORT PROJECT ZIP BUTTON
                                OutlinedButton(
                                    onClick = {
                                        val zip = viewModel.exportProjectZip()
                                        if (zip != null) {
                                            viewModel.shareProjectZip(zip)
                                        }
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("export_project_zip_button")
                                ) {
                                    Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Export Complete Android Project (.zip)")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showHttpWarningDialog) {
        AlertDialog(
            onDismissRequest = { showHttpWarningDialog = false },
            title = { Text("HTTP Security Warning", fontWeight = FontWeight.Bold) },
            text = {
                Text("HTTP websites transmit data in plaintext and are susceptible to interception. Android enforces HTTPS by default. Are you sure you want to proceed with cleartext HTTP?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showHttpWarningDialog = false
                        viewModel.triggerBuild(keyPassInput, storePassInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TechAmber)
                ) {
                    Text("Continue with HTTP")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHttpWarningDialog = false }) {
                    Text("Cancel & Fix URL")
                }
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}

@Composable
fun SettingSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Text(description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = TechCyanPrimary)
        )
    }
}

@Composable
fun UaOptionItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun IconPresetPill(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) TechCyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) TechCyanPrimary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) TechCyanPrimary else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun BuildTypePill(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) TechCyanPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) TechCyanPrimary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) TechCyanPrimary else MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}
