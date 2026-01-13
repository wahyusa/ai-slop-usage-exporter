# AI SLOP : Usage Exporter Android App APK

Android app that I copy pasted from ChatGPT to make it work.
This app will request specific access.
This app will export usage data without root, without ADB, and get full years.

 ## Structure

```
 ai-slop-usage-exporter/
├─ app/
│  ├─ src/main/
│  │  ├─ AndroidManifest.xml
│  │  ├─ java/com/example/usageexporter/
│  │  │    ├─ MainActivity.kt
│  │  │    ├─ data/UsageRepository.kt
│  │  │    ├─ export/ZipExporter.kt
│  │  ├─ res/layout/activity_main.xml
├─ build.gradle.kts
├─ settings.gradle.kts
```

## Build .apk

`./gradlew assembleDebug`

Result in here `app/build/outputs/apk/debug`

## Serve generated .apk when development

`python3 -m http.server -d app/build/outputs/apk/debug`