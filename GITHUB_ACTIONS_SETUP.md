# GitHub Actions Setup - Step by Step

## Prerequisites

- GitHub account (free at github.com)
- Git credentials set up

## Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Repository name: `flashlight-app` (or your preferred name)
3. Make it **Public** (Actions work on public repos for free)
4. Click "Create repository"
5. Copy the HTTPS URL (looks like: `https://github.com/yourusername/flashlight-app.git`)

## Step 2: Push Code to GitHub

From your Termux terminal in the Flashlight folder:

```bash
cd ~/Test1/Flashlight

# Add remote (replace with your repo URL)
git remote add origin https://github.com/YOUR_USERNAME/flashlight-app.git

# Rename branch to main (GitHub default)
git branch -M main

# Push to GitHub
git push -u origin main
```

**You'll be prompted for credentials:**
- Username: Your GitHub username
- Password: Use a **Personal Access Token**, not your password
  - Go to GitHub Settings > Developer settings > Personal access tokens > Tokens (classic)
  - Click "Generate new token (classic)"
  - Check: `repo`, `workflow`
  - Copy token and paste as password

## Step 3: Verify Workflow Runs

1. Go to your repo on GitHub
2. Click **Actions** tab
3. Click on the build workflow that's running
4. Wait 5-10 minutes for build to complete
5. When done, click **Summary** to see artifacts

## Step 4: Download APK

Once build completes:

1. In Actions > Latest workflow run
2. Scroll down to "Artifacts" section
3. Download `app-debug.apk`
4. Transfer to phone and install: `adb install app-debug.apk`

## Automatic Builds on Every Push

After setup, every time you:

```bash
git add .
git commit -m "Your changes"
git push
```

GitHub automatically:
- Checks out your code
- Sets up Java 17
- Runs `./gradlew assembleDebug`
- Uploads APK as artifact
- Build takes 5-10 minutes

## Troubleshooting

**"Authentication failed"**
- Make sure you used a Personal Access Token, not your password
- Token needs `repo` and `workflow` permissions

**"Workflow not running"**
- Check Actions tab - it should show a workflow run
- If not, check `.github/workflows/build.yml` exists in repo

**"Build failed" in Actions**
- Click the workflow run to see error logs
- Common issues: missing app.json, gradle syntax errors
- All files were committed, so shouldn't happen

**"File not found" when downloading**
- Wait until build completes (green checkmark)
- Scroll down in workflow run to "Artifacts" section

## Next Steps

1. Every time you modify code on Termux:
```bash
git add .
git commit -m "Description of changes"
git push
```

2. Wait 5-10 min, then download APK from Actions tab
3. Install on phone: `adb install app-debug.apk`

## Advanced: Tag Releases

To create release versions:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

The workflow is configured to auto-create GitHub releases with APK attached.
