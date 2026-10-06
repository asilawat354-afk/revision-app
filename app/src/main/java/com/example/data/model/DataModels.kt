package com.example.data.model

data class WebProject(
    val id: String,
    val name: String,
    val url: String,
    val packageName: String,
    val versionName: String = "1.0.0",
    val versionCode: Int = 1,
    val author: String = "Web2APK Developer",
    val description: String = "Mobile app created with Web2APK Builder",
    val iconStyle: String = "CYAN_SHIELD", // CYAN_SHIELD, BLUE_GLOBE, PURPLE_ROCKET, EMERALD_CART, MINIMAL_DARK
    val splashEnabled: Boolean = true,
    val splashBgColor: String = "#0B1120",
    val splashDurationMs: Int = 1500,
    val themeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val orientation: String = "AUTO", // AUTO, PORTRAIT, LANDSCAPE, SENSOR
    val enableEdgeToEdge: Boolean = true,
    val webViewConfig: WebViewConfig = WebViewConfig(),
    val permissionsConfig: PermissionsConfig = PermissionsConfig(),
    val signingConfig: SigningConfig = SigningConfig(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class WebViewConfig(
    val enableJavaScript: Boolean = true,
    val enableDomStorage: Boolean = true,
    val enableDatabase: Boolean = true,
    val enableCookies: Boolean = true,
    val enableThirdPartyCookies: Boolean = true,
    val cacheMode: String = "LOAD_DEFAULT", // LOAD_DEFAULT, LOAD_CACHE_ELSE_NETWORK, LOAD_NO_CACHE
    val mixedContentMode: String = "NEVER_ALLOW", // NEVER_ALLOW, ALWAYS_ALLOW, COMPATIBILITY
    val enableZoom: Boolean = true,
    val userAgentMode: String = "DEFAULT", // DEFAULT, MOBILE_CHROME, DESKTOP, CUSTOM
    val customUserAgent: String = "",
    val linkRoutingMode: String = "EXTERNAL_BROWSER", // ALL_INTERNAL, EXTERNAL_BROWSER, WHITELIST_ONLY
    val allowedDomains: String = "",
    val enablePullToRefresh: Boolean = true,
    val enableDownloadManager: Boolean = true,
    val enableFileUpload: Boolean = true,
    val enableFullscreen: Boolean = true,
    val showOfflineScreen: Boolean = true,
    val deepLinkScheme: String = ""
)

data class PermissionsConfig(
    val internet: Boolean = true,
    val camera: Boolean = false,
    val microphone: Boolean = false,
    val location: Boolean = false,
    val notifications: Boolean = false,
    val storageDownloads: Boolean = true
)

data class SigningConfig(
    val isRelease: Boolean = false,
    val keyAlias: String = "releaseKey",
    val keyPassword: String = "",
    val storePassword: String = ""
)

enum class BuildStatus {
    QUEUED,
    PREPARING,
    CONFIGURING,
    RUNNING_GRADLE,
    SIGNING,
    VERIFYING,
    COMPLETED,
    FAILED,
    CANCELLED
}

data class BuildRecord(
    val id: String,
    val projectId: String,
    val appName: String,
    val packageName: String,
    val versionName: String,
    val targetUrl: String,
    val status: BuildStatus,
    val stageMessage: String,
    val progressPercent: Int,
    val logs: String = "",
    val apkFileSize: Long = 0L,
    val apkDownloadUrl: String? = null,
    val buildDurationMs: Long = 0L,
    val createdAt: Long = System.currentTimeMillis(),
    val isRealBuild: Boolean = false,
    val errorMessage: String? = null
)

data class UrlValidationResult(
    val isValid: Boolean,
    val normalizedUrl: String,
    val isHttps: Boolean,
    val title: String? = null,
    val faviconUrl: String? = null,
    val httpCode: Int = 0,
    val responseTimeMs: Long = 0L,
    val errorMessage: String? = null,
    val isSsrfSafe: Boolean = true
)

data class BuildWorkerStatus(
    val isConnected: Boolean = false,
    val workerUrl: String = "http://10.0.2.2:8080",
    val version: String = "1.0.0",
    val supportedGradleVersion: String = "8.5",
    val supportedAndroidSdk: String = "Android 34 / 35",
    val activeJobsCount: Int = 0,
    val maxConcurrentJobs: Int = 4,
    val lastPingTimestamp: Long = 0L,
    val errorMessage: String? = null
)
