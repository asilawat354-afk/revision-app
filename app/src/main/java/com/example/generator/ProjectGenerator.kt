package com.example.generator

import android.content.Context
import com.example.data.model.WebProject
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ProjectGenerator {

    fun generateManifest(project: WebProject): String {
        val permissions = mutableListOf<String>()
        if (project.permissionsConfig.internet) {
            permissions.add("    <uses-permission android:name=\"android.permission.INTERNET\" />")
            permissions.add("    <uses-permission android:name=\"android.permission.ACCESS_NETWORK_STATE\" />")
        }
        if (project.permissionsConfig.camera) {
            permissions.add("    <uses-permission android:name=\"android.permission.CAMERA\" />")
            permissions.add("    <uses-feature android:name=\"android.hardware.camera\" android:required=\"false\" />")
        }
        if (project.permissionsConfig.microphone) {
            permissions.add("    <uses-permission android:name=\"android.permission.RECORD_AUDIO\" />")
            permissions.add("    <uses-feature android:name=\"android.hardware.microphone\" android:required=\"false\" />")
        }
        if (project.permissionsConfig.location) {
            permissions.add("    <uses-permission android:name=\"android.permission.ACCESS_FINE_LOCATION\" />")
            permissions.add("    <uses-permission android:name=\"android.permission.ACCESS_COARSE_LOCATION\" />")
        }
        if (project.permissionsConfig.notifications) {
            permissions.add("    <uses-permission android:name=\"android.permission.POST_NOTIFICATIONS\" />")
        }
        if (project.permissionsConfig.storageDownloads) {
            permissions.add("    <uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\" android:maxSdkVersion=\"28\" />")
        }

        val orientationAttr = when (project.orientation) {
            "PORTRAIT" -> "\n            android:screenOrientation=\"portrait\""
            "LANDSCAPE" -> "\n            android:screenOrientation=\"landscape\""
            "SENSOR" -> "\n            android:screenOrientation=\"sensor\""
            else -> ""
        }

        val allowCleartext = if (project.url.startsWith("http://")) {
            "\n        android:usesCleartextTraffic=\"true\""
        } else ""

        val deepLinkIntent = if (project.webViewConfig.deepLinkScheme.isNotBlank()) {
            """
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="${project.webViewConfig.deepLinkScheme}" />
            </intent-filter>
            """.trimIndent()
        } else ""

        return """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

${permissions.joinToString("\n")}

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Web2ApkGenerated"$allowCleartext>

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:configChanges="orientation|screenSize|screenLayout|keyboardHidden"$orientationAttr
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
            $deepLinkIntent
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${project.packageName}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>
</manifest>
        """.trimIndent()
    }

    fun generateAppBuildGradle(project: WebProject): String {
        return """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "${project.packageName}"
    compileSdk = 34

    defaultConfig {
        applicationId = "${project.packageName}"
        minSdk = 24
        targetSdk = 34
        versionCode = ${project.versionCode}
        versionName = "${project.versionName}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}
        """.trimIndent()
    }

    fun generateRootBuildGradle(): String {
        return """
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
        """.trimIndent()
    }

    fun generateSettingsGradle(project: WebProject): String {
        return """
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "${project.name}"
include(":app")
        """.trimIndent()
    }

    fun generateMainActivity(project: WebProject): String {
        val packageDecl = "package ${project.packageName}"

        val userAgentConfig = when (project.webViewConfig.userAgentMode) {
            "MOBILE_CHROME" -> "settings.userAgentString = settings.userAgentString.replace(\"; wv\", \"\")"
            "DESKTOP" -> "settings.userAgentString = \"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36\""
            "CUSTOM" -> if (project.webViewConfig.customUserAgent.isNotBlank()) "settings.userAgentString = \"${project.webViewConfig.customUserAgent.replace("\"", "\\\"")}\"" else ""
            else -> ""
        }

        val cacheModeSetting = when (project.webViewConfig.cacheMode) {
            "LOAD_CACHE_ELSE_NETWORK" -> "settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK"
            "LOAD_NO_CACHE" -> "settings.cacheMode = WebSettings.LOAD_NO_CACHE"
            else -> "settings.cacheMode = WebSettings.LOAD_DEFAULT"
        }

        val mixedContentSetting = when (project.webViewConfig.mixedContentMode) {
            "ALWAYS_ALLOW" -> "settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW"
            "COMPATIBILITY" -> "settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE"
            else -> "settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW"
        }

        return """
$packageDecl

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var offlineLayout: View
    private lateinit var customViewContainer: FrameLayout

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    private val targetUrl = "${project.url}"
    private val allowedDomains = listOf(${project.webViewConfig.allowedDomains.split(",").filter { it.isNotBlank() }.joinToString { "\"${it.trim()}\"" }})

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val uris = when {
                data?.clipData != null -> {
                    val count = data.clipData!!.itemCount
                    Array(count) { i -> data.clipData!!.getItemAt(i).uri }
                }
                data?.data != null -> arrayOf(data.data!!)
                else -> emptyArray()
            }
            fileUploadCallback?.onReceiveValue(uris)
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Permissions result handled
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        ${if (project.splashEnabled) "installSplashScreen()" else ""}
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupWebView()
        setupBackPress()
        checkRuntimePermissions()

        loadUrl(targetUrl)
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        offlineLayout = findViewById(R.id.offlineLayout)
        customViewContainer = findViewById(R.id.customViewContainer)

        swipeRefresh.isEnabled = ${project.webViewConfig.enablePullToRefresh}
        swipeRefresh.setOnRefreshListener {
            if (isNetworkAvailable()) {
                webView.reload()
            } else {
                swipeRefresh.isRefreshing = false
                showOfflineScreen()
            }
        }

        findViewById<View>(R.id.btnRetryOffline).setOnClickListener {
            if (isNetworkAvailable()) {
                hideOfflineScreen()
                webView.loadUrl(webView.url ?: targetUrl)
            } else {
                Toast.makeText(this, "Still offline. Please check your internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings = webView.settings
        settings.javaScriptEnabled = ${project.webViewConfig.enableJavaScript}
        settings.domStorageEnabled = ${project.webViewConfig.enableDomStorage}
        settings.databaseEnabled = ${project.webViewConfig.enableDatabase}
        settings.setSupportZoom(${project.webViewConfig.enableZoom})
        settings.builtInZoomControls = ${project.webViewConfig.enableZoom}
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        $cacheModeSetting
        $mixedContentSetting
        $userAgentConfig

        // Cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(${project.webViewConfig.enableCookies})
        cookieManager.setAcceptThirdPartyCookies(webView, ${project.webViewConfig.enableThirdPartyCookies})

        // Downloads
        ${if (project.webViewConfig.enableDownloadManager) """
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                val filename = URLUtil.guessFileName(url, contentDisposition, mimetype)
                request.setMimeType(mimetype)
                request.addRequestHeader("User-Agent", userAgent)
                val cookie = CookieManager.getInstance().getCookie(url)
                if (cookie != null) request.addRequestHeader("Cookie", cookie)
                request.setTitle(filename)
                request.setDescription("Downloading file...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "Download started: " + filename, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Download failed: " + e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
        """.trimIndent() else ""}

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                val host = uri.host ?: ""

                // Mode Handling
                ${when (project.webViewConfig.linkRoutingMode) {
                    "ALL_INTERNAL" -> "return false"
                    "WHITELIST_ONLY" -> """
                        if (allowedDomains.any { host.contains(it) }) {
                            return false
                        }
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        startActivity(intent)
                        return true
                    """.trimIndent()
                    else -> """
                        if (host.equals(Uri.parse(targetUrl).host, ignoreCase = true)) {
                            return false
                        }
                        return try {
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                            true
                        } catch (e: Exception) {
                            false
                        }
                    """.trimIndent()
                }}
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE
                hideOfflineScreen()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                swipeRefresh.isRefreshing = false
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    showOfflineScreen()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress >= 100) {
                    progressBar.visibility = View.GONE
                    swipeRefresh.isRefreshing = false
                }
            }

            // File Upload
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                ${if (project.webViewConfig.enableFileUpload) """
                fileUploadCallback?.onReceiveValue(null)
                fileUploadCallback = filePathCallback
                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"
                }
                filePickerLauncher.launch(intent)
                return true
                """.trimIndent() else "return false"}
            }

            // Geolocation
            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                ${if (project.permissionsConfig.location) "callback?.invoke(origin, true, false)" else "callback?.invoke(origin, false, false)"}
            }

            // WebRTC Permissions (Camera/Mic)
            override fun onPermissionRequest(request: PermissionRequest?) {
                val requested = request?.resources ?: return
                val granted = mutableListOf<String>()
                ${if (project.permissionsConfig.camera) "if (requested.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) granted.add(PermissionRequest.RESOURCE_VIDEO_CAPTURE)" else ""}
                ${if (project.permissionsConfig.microphone) "if (requested.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) granted.add(PermissionRequest.RESOURCE_AUDIO_CAPTURE)" else ""}
                if (granted.isNotEmpty()) {
                    request.grant(granted.toTypedArray())
                } else {
                    request.deny()
                }
            }

            // Fullscreen video
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }
                customView = view
                customViewCallback = callback
                customViewContainer.addView(view)
                customViewContainer.visibility = View.VISIBLE
                webView.visibility = View.GONE
            }

            override fun onHideCustomView() {
                if (customView == null) return
                customViewContainer.removeView(customView)
                customViewContainer.visibility = View.GONE
                customView = null
                customViewCallback?.onCustomViewHidden()
                webView.visibility = View.VISIBLE
            }
        }
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (customView != null) {
                    webView.webChromeClient?.onHideCustomView()
                } else if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    showExitDialog()
                }
            }
        })
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit ${project.name}?")
            .setPositiveButton("Exit") { _, _ -> finish() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadUrl(url: String) {
        if (isNetworkAvailable()) {
            webView.loadUrl(url)
        } else {
            showOfflineScreen()
        }
    }

    private fun showOfflineScreen() {
        webView.visibility = View.GONE
        offlineLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun hideOfflineScreen() {
        offlineLayout.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun checkRuntimePermissions() {
        val permissionsToRequest = mutableListOf<String>()
        ${if (project.permissionsConfig.camera) "if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.CAMERA)" else ""}
        ${if (project.permissionsConfig.microphone) "if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)" else ""}
        ${if (project.permissionsConfig.location) "if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)" else ""}
        ${if (project.permissionsConfig.notifications) "if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)" else ""}

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
        """.trimIndent()
    }

    fun generateLayoutXml(): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#0F172A">

    <ProgressBar
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="4dp"
        android:max="100"
        android:progressTint="#0284C7"
        android:visibility="gone" />

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/swipeRefresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="4dp">

        <WebView
            android:id="@+id/webView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

    <!-- Offline layout -->
    <LinearLayout
        android:id="@+id/offlineLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:orientation="vertical"
        android:padding="32dp"
        android:visibility="gone">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="📡"
            android:textSize="56sp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="16dp"
            android:text="No Internet Connection"
            android:textColor="#FFFFFF"
            android:textSize="20sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:gravity="center"
            android:text="Please check your internet connection and try again."
            android:textColor="#94A3B8"
            android:textSize="14sp" />

        <Button
            android:id="@+id/btnRetryOffline"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="24dp"
            android:backgroundTint="#0284C7"
            android:text="Retry Connection"
            android:textColor="#FFFFFF" />

    </LinearLayout>

    <!-- Custom Fullscreen Container -->
    <FrameLayout
        android:id="@+id/customViewContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#000000"
        android:visibility="gone" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
        """.trimIndent()
    }

    fun generateThemesXml(project: WebProject): String {
        return """
<resources>
    <style name="Theme.Web2ApkGenerated" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="android:statusBarColor">#0F172A</item>
        <item name="android:navigationBarColor">#0F172A</item>
        <item name="android:windowLightStatusBar">false</item>
    </style>
</resources>
        """.trimIndent()
    }

    fun generateFilePathsXml(): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="." />
    <external-cache-path name="external_cache" path="." />
    <cache-path name="internal_cache" path="." />
    <files-path name="internal_files" path="." />
</paths>
        """.trimIndent()
    }

    fun generateStringsXml(project: WebProject): String {
        return """
<resources>
    <string name="app_name">${project.name}</string>
</resources>
        """.trimIndent()
    }

    fun generateLauncherBackground(): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0B1120"
        android:pathData="M0,0h108v108h-108z" />
</vector>
        """.trimIndent()
    }

    fun generateLauncherForeground(): String {
        return """
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#0284C7"
        android:pathData="M54,24 L78,34 L78,60 C78,74 68,84 54,88 C40,84 30,74 30,60 L30,34 Z" />
    <path
        android:fillColor="#38BDF8"
        android:pathData="M48,54 L52,58 L62,48"
        android:strokeColor="#FFFFFF"
        android:strokeWidth="3" />
</vector>
        """.trimIndent()
    }

    fun generateGradleWrapperProperties(): String {
        return """
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
        """.trimIndent()
    }

    fun exportProjectAsZip(context: Context, project: WebProject): File {
        val sanitizedProjectName = project.name.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
        val zipFile = File(context.cacheDir, "${sanitizedProjectName}_android_project.zip")
        if (zipFile.exists()) zipFile.delete()

        val packageFolder = project.packageName.replace(".", "/")

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            fun addEntry(path: String, content: String) {
                zos.putNextEntry(ZipEntry(path))
                zos.write(content.toByteArray(Charsets.UTF_8))
                zos.closeEntry()
            }

            // Root files
            addEntry("settings.gradle.kts", generateSettingsGradle(project))
            addEntry("build.gradle.kts", generateRootBuildGradle())
            addEntry("gradle.properties", "org.gradle.jvmargs=-Xmx2048m\nandroid.useAndroidX=true\n")
            addEntry("gradle/wrapper/gradle-wrapper.properties", generateGradleWrapperProperties())

            // App module
            addEntry("app/build.gradle.kts", generateAppBuildGradle(project))
            addEntry("app/proguard-rules.pro", "-keepattributes *Annotation*\n-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }\n")
            addEntry("app/src/main/AndroidManifest.xml", generateManifest(project))

            // Java/Kotlin code
            addEntry("app/src/main/java/$packageFolder/MainActivity.kt", generateMainActivity(project))

            // Resources
            addEntry("app/src/main/res/layout/activity_main.xml", generateLayoutXml())
            addEntry("app/src/main/res/values/strings.xml", generateStringsXml(project))
            addEntry("app/src/main/res/values/themes.xml", generateThemesXml(project))
            addEntry("app/src/main/res/xml/file_paths.xml", generateFilePathsXml())
            addEntry("app/src/main/res/drawable/ic_launcher_background.xml", generateLauncherBackground())
            addEntry("app/src/main/res/drawable/ic_launcher_foreground.xml", generateLauncherForeground())
            addEntry("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml", """
                <?xml version="1.0" encoding="utf-8"?>
                <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
                    <background android:drawable="@drawable/ic_launcher_background" />
                    <foreground android:drawable="@drawable/ic_launcher_foreground" />
                </adaptive-icon>
            """.trimIndent())
            addEntry("app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml", """
                <?xml version="1.0" encoding="utf-8"?>
                <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
                    <background android:drawable="@drawable/ic_launcher_background" />
                    <foreground android:drawable="@drawable/ic_launcher_foreground" />
                </adaptive-icon>
            """.trimIndent())

            // README instructions for user
            addEntry("README.md", """
                # ${project.name} - Android WebView Project
                Generated by Web2APK Builder.

                ## Requirements:
                - Android Studio Hedgehog or newer
                - Android SDK 34
                - JDK 17

                ## How to compile locally:
                1. Open this folder in Android Studio.
                2. Or build using command line:
                   `./gradlew assembleDebug`
                3. The APK will be generated at:
                   `app/build/outputs/apk/debug/app-debug.apk`
            """.trimIndent())
        }

        return zipFile
    }
}
