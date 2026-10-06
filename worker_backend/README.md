# Web2APK Build Worker Server

This is the official Dockerized Android Build Worker for Web2APK Builder.

## What It Does
1. Receives website configuration payloads from the Web2APK Builder frontend or Android client (`POST /api/builds`).
2. Synthesizes a clean, production-grade Android Studio Gradle project in an isolated workspace (`/app/builds/{buildId}`).
3. Invokes real Gradle commands (`./gradlew assembleDebug` or `./gradlew assembleRelease`).
4. Signs the APK with Android `apksigner` and verifies bytecode alignment.
5. Streams real-time progress and logs with masked secrets.
6. Serves the final `.apk` binary file with the correct MIME type (`application/vnd.android.package-archive`).

## How to Run with Docker

```bash
cd worker_backend
docker compose up -d --build
```

The worker service will start on `http://localhost:8080`.

## Testing the Health Check
```bash
curl http://localhost:8080/api/health
```

Expected Response:
```json
{
  "status": "online",
  "version": "1.0.0",
  "gradleVersion": "8.5",
  "androidSdk": "Android SDK 34 (UpsideDownCake)",
  "activeJobs": 0,
  "maxConcurrentJobs": 4
}
```

## Connecting from Android Emulator
In the Web2APK Builder app:
1. Open the **Server** tab.
2. Set the Worker URL to `http://10.0.2.2:8080` (the standard Android emulator alias for host `localhost`).
3. Tap **Test Connection**. Once green, all builds compile directly via Gradle!
