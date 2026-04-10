# Build APK Options

## Reality Check

**Local Termux builds won't work** - AAPT2 and other build tools are x86_64 Linux binaries. Termux runs ARM64, so it can't execute them.

## 3 Working Options

### Option 1: GitHub Actions (Recommended - Free & Unlimited)

1. Push code to GitHub
2. Builds automatically on every push
3. Download APK from Actions tab
4. Free for public repos, unlimited builds

**Setup:**
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/yourusername/flashlight.git
git push -u origin main
```

The workflow file is already at `.github/workflows/build.yml`

Then:
- Go to GitHub repo → Actions tab
- View build logs
- Download artifact: `app-debug.apk`

**Pros:** Free, unlimited, automatic, no local setup
**Cons:** Need GitHub account, 5-10 min build time

---

### Option 2: Desktop Build (Fast)

1. Copy `android/` folder to a machine with Android Studio
2. Open in Android Studio
3. Click Build > Build APK(s)
4. Transfer APK back via USB/file sharing
5. Install with: `adb install app-debug.apk`

**Pros:** Fast (2-3 min), full control, no cloud dependencies
**Cons:** Requires desktop access

---

### Option 3: Expo EAS (Cloud - Limited Free)

1. Create Expo account at expo.dev
2. Run: `eas login`
3. Build: `npm run android`
4. Download APK

**Pros:** Simple, cloud-based, Expo integration
**Cons:** 15 free builds/month, then $99/month

---

## Recommended Workflow

**For development:**
- Code on Termux
- Use GitHub Actions to build (commit + wait 5-10 min)
- Download APK + test on phone

**For rapid testing:**
- Code on desktop with Android Studio
- Build locally (2-3 min)
- Test immediately

**For production:**
- Use GitHub Actions for automated builds
- Tag a release in Git → auto-creates APK release

---

## Install Built APK

```bash
# Download APK from GitHub Actions or EAS
# Then:
adb install -r Flashlight-debug.apk

# Or if ADB not available, transfer via file manager and tap to install
```

---

## Why Local Build Won't Work

- AAPT2: `ELF: not found` = x86_64 binary can't run on ARM64
- d8: Same issue
- This is a fundamental architecture mismatch, not fixable without rebuilding tools for ARM

---

## Summary Table

| Method | Time | Cost | Setup | Recommendation |
|--------|------|------|-------|-----------------|
| GitHub Actions | 5-10 min | Free | Low | ✓ Best |
| Desktop | 2-3 min | Free | Medium | Good |
| Expo EAS | 3-5 min | Free (15x/mo) | Low | Limited |
| Local Termux | N/A | Free | N/A | Won't work |
