# Build APK with Expo EAS (Cloud Build)

## Setup (One-time)

```bash
# Login to Expo account (required for free EAS builds)
eas login

# Or use credentials file:
# Create ~/.expo/credentials.json with:
# {
#   "username": "your-expo-username",
#   "password": "your-expo-password"
# }
```

## Build APK

From the Flashlight folder:

```bash
# Build for preview (quick APK)
npm run android

# Or with eas directly:
eas build --platform android --profile preview

# Build release version (for Google Play):
eas build --platform android --profile production
```

## Download APK

After build completes:
1. EAS provides a download link in the terminal
2. Or download from: https://expo.dev/dashboard
3. Install on phone: `adb install app-version.apk`

## Benefits

✓ No Android SDK/NDK needed on Termux  
✓ Cloud compilation (handled by Expo servers)  
✓ Fast builds (parallel processing)  
✓ No Gradle compatibility issues  
✓ Works on Termux with just npm  

## Install from EAS

```bash
# Download APK file, then:
adb install -r Flashlight-preview.apk
```

Or use EAS's direct install (requires expo account):
```bash
eas build --platform android --profile preview --wait
```

## Troubleshooting

**"No authentication token"**: Run `eas login` first

**"Project not configured"**: eas.json already exists in the project

**Build fails**: Check app.json for syntax errors or missing permissions

## Free Tier Limits

EAS offers free builds for Expo projects. If you hit limits, consider:
- Using the preview profile (faster builds)
- Building on desktop with local Gradle as fallback
