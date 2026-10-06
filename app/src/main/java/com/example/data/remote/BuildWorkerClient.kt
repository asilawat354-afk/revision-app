package com.example.data.remote

import com.example.data.model.BuildRecord
import com.example.data.model.BuildStatus
import com.example.data.model.BuildWorkerStatus
import com.example.data.model.WebProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object BuildWorkerClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    suspend fun checkHealth(baseUrl: String): BuildWorkerStatus = withContext(Dispatchers.IO) {
        val sanitizedBase = baseUrl.trim().removeSuffix("/")
        val healthUrl = "$sanitizedBase/api/health"

        try {
            val request = Request.Builder()
                .url(healthUrl)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: "{}"
                    val json = JSONObject(body)
                    BuildWorkerStatus(
                        isConnected = true,
                        workerUrl = sanitizedBase,
                        version = json.optString("version", "1.0.0"),
                        supportedGradleVersion = json.optString("gradleVersion", "8.5"),
                        supportedAndroidSdk = json.optString("androidSdk", "Android 34"),
                        activeJobsCount = json.optInt("activeJobs", 0),
                        maxConcurrentJobs = json.optInt("maxConcurrentJobs", 4),
                        lastPingTimestamp = System.currentTimeMillis(),
                        errorMessage = null
                    )
                } else {
                    BuildWorkerStatus(
                        isConnected = false,
                        workerUrl = sanitizedBase,
                        lastPingTimestamp = System.currentTimeMillis(),
                        errorMessage = "Worker returned HTTP ${response.code}: ${response.message}"
                    )
                }
            }
        } catch (e: Exception) {
            BuildWorkerStatus(
                isConnected = false,
                workerUrl = sanitizedBase,
                lastPingTimestamp = System.currentTimeMillis(),
                errorMessage = "Connection failed: ${e.localizedMessage ?: "Worker unreachable"}"
            )
        }
    }

    suspend fun submitBuildJob(
        baseUrl: String,
        project: WebProject,
        keyPassword: String,
        storePassword: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val sanitizedBase = baseUrl.trim().removeSuffix("/")
        val submitUrl = "$sanitizedBase/api/builds"

        try {
            val jsonPayload = JSONObject().apply {
                put("projectId", project.id)
                put("appName", project.name)
                put("packageName", project.packageName)
                put("url", project.url)
                put("versionName", project.versionName)
                put("versionCode", project.versionCode)
                put("author", project.author)
                put("description", project.description)
                put("iconStyle", project.iconStyle)
                put("themeMode", project.themeMode)
                put("orientation", project.orientation)
                put("enableEdgeToEdge", project.enableEdgeToEdge)

                // Splash
                put("splash", JSONObject().apply {
                    put("enabled", project.splashEnabled)
                    put("bgColor", project.splashBgColor)
                    put("durationMs", project.splashDurationMs)
                })

                // WebView
                put("webView", JSONObject().apply {
                    put("javascript", project.webViewConfig.enableJavaScript)
                    put("domStorage", project.webViewConfig.enableDomStorage)
                    put("database", project.webViewConfig.enableDatabase)
                    put("cookies", project.webViewConfig.enableCookies)
                    put("thirdPartyCookies", project.webViewConfig.enableThirdPartyCookies)
                    put("cacheMode", project.webViewConfig.cacheMode)
                    put("mixedContent", project.webViewConfig.mixedContentMode)
                    put("zoom", project.webViewConfig.enableZoom)
                    put("userAgentMode", project.webViewConfig.userAgentMode)
                    put("customUserAgent", project.webViewConfig.customUserAgent)
                    put("linkRoutingMode", project.webViewConfig.linkRoutingMode)
                    put("allowedDomains", project.webViewConfig.allowedDomains)
                    put("pullToRefresh", project.webViewConfig.enablePullToRefresh)
                    put("downloadManager", project.webViewConfig.enableDownloadManager)
                    put("fileUpload", project.webViewConfig.enableFileUpload)
                    put("fullscreen", project.webViewConfig.enableFullscreen)
                    put("showOfflineScreen", project.webViewConfig.showOfflineScreen)
                    put("deepLinkScheme", project.webViewConfig.deepLinkScheme)
                })

                // Permissions
                put("permissions", JSONObject().apply {
                    put("internet", project.permissionsConfig.internet)
                    put("camera", project.permissionsConfig.camera)
                    put("microphone", project.permissionsConfig.microphone)
                    put("location", project.permissionsConfig.location)
                    put("notifications", project.permissionsConfig.notifications)
                    put("storageDownloads", project.permissionsConfig.storageDownloads)
                })

                // Signing
                put("signing", JSONObject().apply {
                    put("isRelease", project.signingConfig.isRelease)
                    put("keyAlias", project.signingConfig.keyAlias)
                    if (keyPassword.isNotBlank()) put("keyPassword", keyPassword)
                    if (storePassword.isNotBlank()) put("storePassword", storePassword)
                })
            }

            val request = Request.Builder()
                .url(submitUrl)
                .post(jsonPayload.toString().toRequestBody(JSON_MEDIA))
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val respJson = JSONObject(body)
                    val jobId = respJson.optString("buildId", "")
                    if (jobId.isNotBlank()) {
                        Result.success(jobId)
                    } else {
                        Result.failure(Exception("Worker response did not include a valid buildId"))
                    }
                } else {
                    Result.failure(Exception("Worker error (${response.code}): $body"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pollJobStatus(baseUrl: String, buildId: String): Result<BuildRecord> = withContext(Dispatchers.IO) {
        val sanitizedBase = baseUrl.trim().removeSuffix("/")
        val statusUrl = "$sanitizedBase/api/builds/$buildId/status"

        try {
            val request = Request.Builder().url(statusUrl).get().build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val statusStr = json.optString("status", "FAILED")
                    val status = try { BuildStatus.valueOf(statusStr) } catch (e: Exception) { BuildStatus.FAILED }
                    val downloadUrl = if (status == BuildStatus.COMPLETED) {
                        "$sanitizedBase/api/builds/$buildId/download"
                    } else null

                    val record = BuildRecord(
                        id = buildId,
                        projectId = json.optString("projectId", ""),
                        appName = json.optString("appName", ""),
                        packageName = json.optString("packageName", ""),
                        versionName = json.optString("versionName", "1.0.0"),
                        targetUrl = json.optString("targetUrl", ""),
                        status = status,
                        stageMessage = json.optString("stageMessage", ""),
                        progressPercent = json.optInt("progressPercent", 0),
                        logs = json.optString("logs", ""),
                        apkFileSize = json.optLong("apkFileSize", 0L),
                        apkDownloadUrl = downloadUrl,
                        buildDurationMs = json.optLong("buildDurationMs", 0L),
                        isRealBuild = true,
                        errorMessage = if (json.has("errorMessage")) json.optString("errorMessage") else null
                    )
                    Result.success(record)
                } else {
                    Result.failure(Exception("Status poll failed (${response.code}): $body"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelJob(baseUrl: String, buildId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val sanitizedBase = baseUrl.trim().removeSuffix("/")
        val cancelUrl = "$sanitizedBase/api/builds/$buildId/cancel"

        try {
            val request = Request.Builder()
                .url(cancelUrl)
                .post("{}".toRequestBody(JSON_MEDIA))
                .build()
            client.newCall(request).execute().use { response ->
                Result.success(response.isSuccessful)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
