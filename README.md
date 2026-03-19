# FraudGuard - SMS Fraud Detection Android App

## ✅ Quick Setup (READ THIS FIRST)

### Step 1 — Open the project
1. Unzip `FraudGuard_AndroidProject.zip`
2. Open **Android Studio**
3. Click **File → Open** → select the `FraudGuard` folder
4. Click **OK**

### Step 2 — If "Importing Gradle project" is stuck
This is caused by the gradle-wrapper.jar needing to be regenerated.
**Do this immediately:**

1. Go to **File → Settings** (Windows) or **Android Studio → Preferences** (Mac)
2. Go to **Build, Execution, Deployment → Build Tools → Gradle**
3. Under **Gradle JDK**, select **Embedded JDK** (recommended)
4. Under **Use Gradle from**, select **'wrapper' task in Gradle build script**
5. Click **OK** then click **File → Sync Project with Gradle Files**

**OR try this faster fix:**
1. In Android Studio, click **Help → Find Action** (Ctrl+Shift+A)
2. Type **"Gradle Wrapper"** and select **"Generate Gradle Wrapper"**  
3. This regenerates the wrapper.jar automatically
4. Then click **Sync Now**

### Step 3 — If you see "Java version incompatible"
1. Go to **File → Project Structure → SDK Location**
2. Under **Gradle Settings**, set **Gradle JDK** to **JDK 17** or **Embedded JDK**
3. Click **Apply → OK → Sync Now**

### Step 4 — Run the app
- Connect an Android device or start an emulator
- Press ▶ **Run** button
- Login with: `admin@fraudapp.com` / `admin123`

---

## Compatibility
| Component | Version |
|---|---|
| Android Studio | Hedgehog / Iguana / Ladybug / any recent |
| Gradle | 8.9 (auto-downloaded) |
| AGP | 8.3.2 |
| Java / JDK | 17 or 21 (both work) |
| Min Android | API 24 (Android 7.0) |
| Target Android | API 34 (Android 14) |

---

## Project Structure
```
FraudGuard/
├── app/src/main/java/com/example/fraudapp/
│   ├── LoginActivity.kt
│   ├── DashboardActivity.kt
│   ├── SmsDetectionActivity.kt
│   ├── ResultActivity.kt
│   ├── HistoryActivity.kt
│   ├── adapter/MessageAdapter.kt
│   ├── data/DataRepository.kt
│   └── model/MessageModel.kt
├── app/src/main/res/layout/     ← All XML layouts
├── app/build.gradle             ← App dependencies
├── build.gradle                 ← Project plugins
├── settings.gradle              ← Repo config + JitPack
└── gradle/wrapper/
    └── gradle-wrapper.properties ← Gradle 8.9
```

## Demo Credentials
- Email: `admin@fraudapp.com`
- Password: `admin123`
