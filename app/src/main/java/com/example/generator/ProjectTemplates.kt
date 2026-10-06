package com.example.generator

import com.example.data.model.PermissionsConfig
import com.example.data.model.WebProject
import com.example.data.model.WebViewConfig

data class ProjectTemplate(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val iconEmoji: String,
    val defaultConfig: WebProject
)

object ProjectTemplates {
    val list = listOf(
        ProjectTemplate(
            id = "standard",
            title = "Standard WebApp",
            subtitle = "Recommended for blogs, news, SaaS",
            description = "Safe configuration with HTTPS, pull-to-refresh, progress indicator, download manager, and back navigation.",
            iconEmoji = "🌐",
            defaultConfig = WebProject(
                id = "",
                name = "My WebApp",
                url = "https://example.com",
                packageName = "com.example.webapp",
                iconStyle = "CYAN_SHIELD",
                webViewConfig = WebViewConfig(
                    enableJavaScript = true,
                    enableDomStorage = true,
                    enableCookies = true,
                    enablePullToRefresh = true,
                    enableDownloadManager = true,
                    enableFileUpload = true,
                    linkRoutingMode = "EXTERNAL_BROWSER"
                ),
                permissionsConfig = PermissionsConfig(
                    internet = true,
                    storageDownloads = true
                )
            )
        ),
        ProjectTemplate(
            id = "fullscreen_game",
            title = "Fullscreen & Game",
            subtitle = "For HTML5 games & interactive media",
            description = "Immersive fullscreen mode, hardware acceleration, DOM storage, sensor orientation, and disabled pull-to-refresh.",
            iconEmoji = "🎮",
            defaultConfig = WebProject(
                id = "",
                name = "Web Game",
                url = "https://example.com/game",
                packageName = "com.example.webgame",
                iconStyle = "PURPLE_ROCKET",
                orientation = "SENSOR",
                webViewConfig = WebViewConfig(
                    enableJavaScript = true,
                    enableDomStorage = true,
                    enableDatabase = true,
                    enablePullToRefresh = false,
                    enableFullscreen = true,
                    userAgentMode = "MOBILE_CHROME"
                ),
                permissionsConfig = PermissionsConfig(
                    internet = true,
                    storageDownloads = false
                )
            )
        ),
        ProjectTemplate(
            id = "pwa_wrapper",
            title = "PWA Wrapper",
            subtitle = "For Progressive Web Apps with offline cache",
            description = "Aggressive offline caching, local DOM storage, service worker friendly headers, and splash screen matching PWA manifest.",
            iconEmoji = "⚡",
            defaultConfig = WebProject(
                id = "",
                name = "My PWA",
                url = "https://example.com/pwa",
                packageName = "com.example.pwa",
                iconStyle = "BLUE_GLOBE",
                splashDurationMs = 1200,
                webViewConfig = WebViewConfig(
                    enableJavaScript = true,
                    enableDomStorage = true,
                    enableDatabase = true,
                    cacheMode = "LOAD_DEFAULT",
                    enablePullToRefresh = true,
                    linkRoutingMode = "ALL_INTERNAL"
                ),
                permissionsConfig = PermissionsConfig(
                    internet = true,
                    notifications = true,
                    storageDownloads = true
                )
            )
        ),
        ProjectTemplate(
            id = "ecommerce",
            title = "E-Commerce & Store",
            subtitle = "For shops with payments, camera & location",
            description = "Equipped with file chooser for payment receipts, camera for barcode/QR scanning, geolocation for deliveries, and external bank app routing.",
            iconEmoji = "🛍️",
            defaultConfig = WebProject(
                id = "",
                name = "My Store",
                url = "https://example.com/shop",
                packageName = "com.example.store",
                iconStyle = "EMERALD_CART",
                webViewConfig = WebViewConfig(
                    enableJavaScript = true,
                    enableDomStorage = true,
                    enableCookies = true,
                    enableThirdPartyCookies = true,
                    enableFileUpload = true,
                    enableDownloadManager = true,
                    linkRoutingMode = "EXTERNAL_BROWSER"
                ),
                permissionsConfig = PermissionsConfig(
                    internet = true,
                    camera = true,
                    location = true,
                    notifications = true,
                    storageDownloads = true
                )
            )
        )
    )
}
