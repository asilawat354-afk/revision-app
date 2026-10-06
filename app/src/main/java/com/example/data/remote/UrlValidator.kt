package com.example.data.remote

import android.net.Uri
import com.example.data.model.UrlValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.InetAddress
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object UrlValidator {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val PRIVATE_IP_PATTERNS = listOf(
        Pattern.compile("^127\\."),
        Pattern.compile("^10\\."),
        Pattern.compile("^192\\.168\\."),
        Pattern.compile("^172\\.(1[6-9]|2[0-9]|3[0-1])\\."),
        Pattern.compile("^169\\.254\\."),
        Pattern.compile("^0\\."),
        Pattern.compile("^::1$"),
        Pattern.compile("^fc00:"),
        Pattern.compile("^fe80:")
    )

    suspend fun validate(rawUrl: String): UrlValidationResult = withContext(Dispatchers.IO) {
        val trimmed = rawUrl.trim()
        if (trimmed.isBlank()) {
            return@withContext UrlValidationResult(
                isValid = false,
                normalizedUrl = trimmed,
                isHttps = false,
                errorMessage = "Website URL cannot be empty.",
                isSsrfSafe = true
            )
        }

        // Scheme check
        val normalized = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }

        val uri = try {
            Uri.parse(normalized)
        } catch (e: Exception) {
            return@withContext UrlValidationResult(
                isValid = false,
                normalizedUrl = normalized,
                isHttps = normalized.startsWith("https://"),
                errorMessage = "Malformed URL format.",
                isSsrfSafe = false
            )
        }

        val host = uri.host
        if (host.isNullOrBlank()) {
            return@withContext UrlValidationResult(
                isValid = false,
                normalizedUrl = normalized,
                isHttps = normalized.startsWith("https://"),
                errorMessage = "Invalid domain name or hostname.",
                isSsrfSafe = false
            )
        }

        // SSRF Check - Prevent internal network attacks
        if (host.equals("localhost", ignoreCase = true) ||
            host.equals("127.0.0.1") ||
            host.equals("0.0.0.0") ||
            host.endsWith(".local") ||
            host.endsWith(".internal")
        ) {
            return@withContext UrlValidationResult(
                isValid = false,
                normalizedUrl = normalized,
                isHttps = normalized.startsWith("https://"),
                errorMessage = "Security Error: Local and loopback addresses (localhost/127.0.0.1) are rejected for SSRF safety.",
                isSsrfSafe = false
            )
        }

        // Resolve IP and verify not private range
        try {
            val addresses = InetAddress.getAllByName(host)
            for (addr in addresses) {
                val ip = addr.hostAddress ?: ""
                for (pattern in PRIVATE_IP_PATTERNS) {
                    if (pattern.matcher(ip).find() || addr.isLoopbackAddress || addr.isSiteLocalAddress || addr.isLinkLocalAddress) {
                        return@withContext UrlValidationResult(
                            isValid = false,
                            normalizedUrl = normalized,
                            isHttps = normalized.startsWith("https://"),
                            errorMessage = "Security Notice: Resolved to private network address ($ip). Only public websites can be built.",
                            isSsrfSafe = false
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // DNS resolution failure
            return@withContext UrlValidationResult(
                isValid = false,
                normalizedUrl = normalized,
                isHttps = normalized.startsWith("https://"),
                errorMessage = "DNS resolution failed: Host '$host' could not be resolved. Please verify domain name.",
                isSsrfSafe = true
            )
        }

        // Probe Website connectivity
        val startTime = System.currentTimeMillis()
        try {
            val request = Request.Builder()
                .url(normalized)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Web2APK Probe) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - startTime
                val code = response.code
                val isHttps = normalized.startsWith("https://", ignoreCase = true)

                var pageTitle: String? = null
                var faviconUrl: String? = null

                if (response.isSuccessful) {
                    val bodyString = response.body?.string() ?: ""
                    // Extract <title>
                    val titleMatcher = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE).matcher(bodyString)
                    if (titleMatcher.find()) {
                        pageTitle = titleMatcher.group(1)?.trim()
                    }

                    // Guess favicon
                    faviconUrl = if (bodyString.contains("rel=\"icon\"") || bodyString.contains("rel=\"shortcut icon\"")) {
                        val iconMatcher = Pattern.compile("<link[^>]*rel=[\"'](?:shortcut )?icon[\"'][^>]*href=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE).matcher(bodyString)
                        if (iconMatcher.find()) {
                            val iconHref = iconMatcher.group(1)
                            if (iconHref != null && iconHref.startsWith("http")) iconHref
                            else if (iconHref != null && iconHref.startsWith("//")) "https:$iconHref"
                            else "${uri.scheme}://${uri.host}${if (iconHref?.startsWith("/") == true) "" else "/"}$iconHref"
                        } else {
                            "${uri.scheme}://${uri.host}/favicon.ico"
                        }
                    } else {
                        "${uri.scheme}://${uri.host}/favicon.ico"
                    }
                }

                UrlValidationResult(
                    isValid = response.isSuccessful || code in 200..399,
                    normalizedUrl = response.request.url.toString(),
                    isHttps = isHttps,
                    title = pageTitle ?: host,
                    faviconUrl = faviconUrl,
                    httpCode = code,
                    responseTimeMs = duration,
                    errorMessage = if (response.isSuccessful || code in 200..399) null else "Server returned HTTP status code $code",
                    isSsrfSafe = true
                )
            }
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            UrlValidationResult(
                isValid = false,
                normalizedUrl = normalized,
                isHttps = normalized.startsWith("https://"),
                responseTimeMs = duration,
                errorMessage = "Network connection failed: ${e.localizedMessage ?: "Connection timed out"}",
                isSsrfSafe = true
            )
        }
    }
}
