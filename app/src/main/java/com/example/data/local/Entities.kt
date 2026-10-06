package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BuildRecord
import com.example.data.model.BuildStatus
import com.example.data.model.PermissionsConfig
import com.example.data.model.SigningConfig
import com.example.data.model.WebProject
import com.example.data.model.WebViewConfig

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val iconStyle: String,
    val splashEnabled: Boolean,
    val splashBgColor: String,
    val splashDurationMs: Int,
    val themeMode: String,
    val orientation: String,
    val enableEdgeToEdge: Boolean,
    // WebView config
    val enableJavaScript: Boolean,
    val enableDomStorage: Boolean,
    val enableDatabase: Boolean,
    val enableCookies: Boolean,
    val enableThirdPartyCookies: Boolean,
    val cacheMode: String,
    val mixedContentMode: String,
    val enableZoom: Boolean,
    val userAgentMode: String,
    val customUserAgent: String,
    val linkRoutingMode: String,
    val allowedDomains: String,
    val enablePullToRefresh: Boolean,
    val enableDownloadManager: Boolean,
    val enableFileUpload: Boolean,
    val enableFullscreen: Boolean,
    val showOfflineScreen: Boolean,
    val deepLinkScheme: String,
    // Permissions config
    val permInternet: Boolean,
    val permCamera: Boolean,
    val permMicrophone: Boolean,
    val permLocation: Boolean,
    val permNotifications: Boolean,
    val permStorageDownloads: Boolean,
    // Signing config
    val isRelease: Boolean,
    val keyAlias: String,
    // Timestamps
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): WebProject {
        return WebProject(
            id = id,
            name = name,
            url = url,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            author = author,
            description = description,
            iconStyle = iconStyle,
            splashEnabled = splashEnabled,
            splashBgColor = splashBgColor,
            splashDurationMs = splashDurationMs,
            themeMode = themeMode,
            orientation = orientation,
            enableEdgeToEdge = enableEdgeToEdge,
            webViewConfig = WebViewConfig(
                enableJavaScript = enableJavaScript,
                enableDomStorage = enableDomStorage,
                enableDatabase = enableDatabase,
                enableCookies = enableCookies,
                enableThirdPartyCookies = enableThirdPartyCookies,
                cacheMode = cacheMode,
                mixedContentMode = mixedContentMode,
                enableZoom = enableZoom,
                userAgentMode = userAgentMode,
                customUserAgent = customUserAgent,
                linkRoutingMode = linkRoutingMode,
                allowedDomains = allowedDomains,
                enablePullToRefresh = enablePullToRefresh,
                enableDownloadManager = enableDownloadManager,
                enableFileUpload = enableFileUpload,
                enableFullscreen = enableFullscreen,
                showOfflineScreen = showOfflineScreen,
                deepLinkScheme = deepLinkScheme
            ),
            permissionsConfig = PermissionsConfig(
                internet = permInternet,
                camera = permCamera,
                microphone = permMicrophone,
                location = permLocation,
                notifications = permNotifications,
                storageDownloads = permStorageDownloads
            ),
            signingConfig = SigningConfig(
                isRelease = isRelease,
                keyAlias = keyAlias,
                keyPassword = "", // Never stored persistently for safety
                storePassword = ""
            ),
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(project: WebProject): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                name = project.name,
                url = project.url,
                packageName = project.packageName,
                versionName = project.versionName,
                versionCode = project.versionCode,
                author = project.author,
                description = project.description,
                iconStyle = project.iconStyle,
                splashEnabled = project.splashEnabled,
                splashBgColor = project.splashBgColor,
                splashDurationMs = project.splashDurationMs,
                themeMode = project.themeMode,
                orientation = project.orientation,
                enableEdgeToEdge = project.enableEdgeToEdge,
                enableJavaScript = project.webViewConfig.enableJavaScript,
                enableDomStorage = project.webViewConfig.enableDomStorage,
                enableDatabase = project.webViewConfig.enableDatabase,
                enableCookies = project.webViewConfig.enableCookies,
                enableThirdPartyCookies = project.webViewConfig.enableThirdPartyCookies,
                cacheMode = project.webViewConfig.cacheMode,
                mixedContentMode = project.webViewConfig.mixedContentMode,
                enableZoom = project.webViewConfig.enableZoom,
                userAgentMode = project.webViewConfig.userAgentMode,
                customUserAgent = project.webViewConfig.customUserAgent,
                linkRoutingMode = project.webViewConfig.linkRoutingMode,
                allowedDomains = project.webViewConfig.allowedDomains,
                enablePullToRefresh = project.webViewConfig.enablePullToRefresh,
                enableDownloadManager = project.webViewConfig.enableDownloadManager,
                enableFileUpload = project.webViewConfig.enableFileUpload,
                enableFullscreen = project.webViewConfig.enableFullscreen,
                showOfflineScreen = project.webViewConfig.showOfflineScreen,
                deepLinkScheme = project.webViewConfig.deepLinkScheme,
                permInternet = project.permissionsConfig.internet,
                permCamera = project.permissionsConfig.camera,
                permMicrophone = project.permissionsConfig.microphone,
                permLocation = project.permissionsConfig.location,
                permNotifications = project.permissionsConfig.notifications,
                permStorageDownloads = project.permissionsConfig.storageDownloads,
                isRelease = project.signingConfig.isRelease,
                keyAlias = project.signingConfig.keyAlias,
                createdAt = project.createdAt,
                updatedAt = project.updatedAt
            )
        }
    }
}

@Entity(tableName = "builds")
data class BuildEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val appName: String,
    val packageName: String,
    val versionName: String,
    val targetUrl: String,
    val status: String,
    val stageMessage: String,
    val progressPercent: Int,
    val logs: String,
    val apkFileSize: Long,
    val apkDownloadUrl: String?,
    val buildDurationMs: Long,
    val createdAt: Long,
    val isRealBuild: Boolean,
    val errorMessage: String?
) {
    fun toDomain(): BuildRecord {
        return BuildRecord(
            id = id,
            projectId = projectId,
            appName = appName,
            packageName = packageName,
            versionName = versionName,
            targetUrl = targetUrl,
            status = try { BuildStatus.valueOf(status) } catch (e: Exception) { BuildStatus.FAILED },
            stageMessage = stageMessage,
            progressPercent = progressPercent,
            logs = logs,
            apkFileSize = apkFileSize,
            apkDownloadUrl = apkDownloadUrl,
            buildDurationMs = buildDurationMs,
            createdAt = createdAt,
            isRealBuild = isRealBuild,
            errorMessage = errorMessage
        )
    }

    companion object {
        fun fromDomain(build: BuildRecord): BuildEntity {
            return BuildEntity(
                id = build.id,
                projectId = build.projectId,
                appName = build.appName,
                packageName = build.packageName,
                versionName = build.versionName,
                targetUrl = build.targetUrl,
                status = build.status.name,
                stageMessage = build.stageMessage,
                progressPercent = build.progressPercent,
                logs = build.logs,
                apkFileSize = build.apkFileSize,
                apkDownloadUrl = build.apkDownloadUrl,
                buildDurationMs = build.buildDurationMs,
                createdAt = build.createdAt,
                isRealBuild = build.isRealBuild,
                errorMessage = build.errorMessage
            )
        }
    }
}
