package com.example

import com.example.data.model.PermissionsConfig
import com.example.data.model.SigningConfig
import com.example.data.model.WebProject
import com.example.data.model.WebViewConfig
import com.example.generator.ProjectGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testPackageNameValidation() {
        val packageRegex = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")

        assertTrue(packageRegex.matches("com.example.app"))
        assertTrue(packageRegex.matches("org.web2apk.store"))
        assertTrue(packageRegex.matches("io.github.my_app"))
        assertTrue(packageRegex.matches("com.example"))

        assertFalse(packageRegex.matches("example")) // Single segment without dot
        assertFalse(packageRegex.matches("com.Example.App")) // Uppercase
        assertFalse(packageRegex.matches("com.example.app-name")) // Dash
        assertFalse(packageRegex.matches("com..app")) // Empty segment
        assertFalse(packageRegex.matches("123.com.app")) // Starting with number
        assertFalse(packageRegex.matches("")) // Empty
    }

    @Test
    fun testProjectGeneratorManifest() {
        val project = WebProject(
            id = "test_1",
            name = "Test WebApp",
            url = "https://example.com",
            packageName = "com.test.webapp",
            permissionsConfig = PermissionsConfig(
                internet = true,
                camera = true,
                microphone = true,
                location = true,
                notifications = true,
                storageDownloads = true
            )
        )

        val manifest = ProjectGenerator.generateManifest(project)
        assertTrue(manifest.contains("android.permission.INTERNET"))
        assertTrue(manifest.contains("android.permission.CAMERA"))
        assertTrue(manifest.contains("android.permission.RECORD_AUDIO"))
        assertTrue(manifest.contains("android.permission.ACCESS_FINE_LOCATION"))
        assertTrue(manifest.contains("android.permission.POST_NOTIFICATIONS"))
        assertTrue(manifest.contains("com.test.webapp.fileprovider"))
    }

    @Test
    fun testProjectGeneratorGradle() {
        val project = WebProject(
            id = "test_2",
            name = "Test App",
            url = "https://example.com",
            packageName = "com.test.mycustomapp",
            versionCode = 5,
            versionName = "2.1.0"
        )

        val appGradle = ProjectGenerator.generateAppBuildGradle(project)
        assertTrue(appGradle.contains("namespace = \"com.test.mycustomapp\""))
        assertTrue(appGradle.contains("applicationId = \"com.test.mycustomapp\""))
        assertTrue(appGradle.contains("versionCode = 5"))
        assertTrue(appGradle.contains("versionName = \"2.1.0\""))
        assertTrue(appGradle.contains("androidx.webkit:webkit"))
    }

    @Test
    fun testProjectGeneratorMainActivity() {
        val project = WebProject(
            id = "test_3",
            name = "Custom WebView",
            url = "https://mysite.org",
            packageName = "org.mysite.app",
            webViewConfig = WebViewConfig(
                enableJavaScript = true,
                enableDomStorage = true,
                enablePullToRefresh = true,
                enableDownloadManager = true,
                userAgentMode = "MOBILE_CHROME",
                linkRoutingMode = "EXTERNAL_BROWSER"
            )
        )

        val mainActivity = ProjectGenerator.generateMainActivity(project)
        assertTrue(mainActivity.contains("package org.mysite.app"))
        assertTrue(mainActivity.contains("settings.javaScriptEnabled = true"))
        assertTrue(mainActivity.contains("settings.domStorageEnabled = true"))
        assertTrue(mainActivity.contains("settings.userAgentString = settings.userAgentString.replace(\"; wv\", \"\")"))
        assertTrue(mainActivity.contains("DownloadManager.Request"))
        assertTrue(mainActivity.contains("https://mysite.org"))
    }

    @Test
    fun testHttpCleartextHandling() {
        val httpProject = WebProject(
            id = "test_http",
            name = "Insecure Site",
            url = "http://insecure-site.com",
            packageName = "com.insecure.site"
        )

        val manifest = ProjectGenerator.generateManifest(httpProject)
        assertTrue(manifest.contains("android:usesCleartextTraffic=\"true\""))
    }
}
