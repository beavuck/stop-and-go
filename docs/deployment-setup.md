# Deployment Setup Guide

This guide explains the Fastlane and GitLab CI setup for deploying the Stop and Go app.

## Overview

The deployment pipeline has three main jobs:
- **build**: Creates signed APK and AAB files on main branch or when a tag is pushed
- **beta**: Uploads to Google Play internal testing track (on main branch)
- **release**: Uploads to Google Play production track (on git tags)

## CI/CD Pipeline Flow

```
1. Tag created (e.g., v1.0.0)
   ↓
2. build job runs
   - Downloads secure files (keystore, etc.)
   - Runs `fastlane build`
   - Creates signed APK and AAB
   - Saves as artifacts
   ↓
3. release job runs
   - Downloads secure files
   - Runs `fastlane update_version` (updates versionCode and versionName)
   - Runs `fastlane release`
   - Uploads AAB to Google Play production track
```

## Required Setup

### 1. Keystore Files

Upload these files to GitLab Secure Files (Settings > CI/CD > Secure Files):

1. **release-keystore.jks** - Your signing keystore
2. **release-keystore.properties** - Keystore properties file:
   ```properties
   storeFile=.secure_files/release-keystore.jks
   storePassword=your_store_password
   keyAlias=release
   keyPassword=your_key_password
   ```

### 2. Google Play Service Account

For `upload_to_play_store` to work, you need:

1. Create a Google Play service account:
   - Go to Google Play Console > Setup > API access
   - Create a new service account
   - Grant "Release to production, exclude devices, and use Play App Signing" permission
   - Download the JSON key file

2. Upload the JSON file to GitLab Secure Files as `play-store-credentials.json`

3. Update the Fastfile to use it:
   ```ruby
   upload_to_play_store(
       track: 'production',
       aab: 'app/build/outputs/bundle/release/app-release.aab',
       json_key: '.secure_files/play-store-credentials.json'
   )
   ```

### 3. Environment Variables (Optional)

If you don't want to use secure files, you can use CI/CD variables:
- `ANDROID_KEYSTORE_FILE` (base64 encoded keystore)
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
- `PLAY_STORE_JSON_KEY` (base64 encoded service account JSON)

## How to Deploy

### Beta Deployment (Internal Testing)

Push to main branch:
```bash
git push origin main
```

This will:
1. Run tests
2. Build signed APK/AAB
3. Upload to Google Play internal testing track as draft

### Production Deployment

Create and push a tag:
```bash
git tag v1.0.0
git push origin v1.0.0
```

This will:
1. Run tests
2. Build signed APK/AAB
3. Update versionCode (increment by 1) and versionName (from tag, e.g., "1.0.0")
4. Upload to Google Play production track

## Fastlane Lanes

### `fastlane build`
- Cleans previous builds
- Assembles release APK
- Bundles release AAB (Android App Bundle)

### `fastlane update_version`
- Extracts version from CI_COMMIT_TAG (e.g., "v1.0.0" → "1.0.0")
- Increments versionCode by 1
- Sets versionName to the tag version

### `fastlane beta`
- Uploads AAB to Google Play internal testing track
- Creates as draft (requires manual promotion)

### `fastlane release`
- Uploads AAB to Google Play production track
- Requires manual review in Play Console before going live

## Troubleshooting

### Build fails with "missing required property storeFile"
- Ensure `release-keystore.jks` and `release-keystore.properties` are uploaded to GitLab Secure Files
- Check that the `build` job downloads them with `glab securefile download --all`

### Upload to Play Store fails
- Ensure you've created a Google Play service account
- Verify the service account has the correct permissions
- Check that `play-store-credentials.json` is uploaded to GitLab Secure Files
- Update Fastfile to reference the JSON key file

### Version conflicts
- Google Play requires each upload to have a unique, incrementing versionCode
- If you get a version conflict, manually increment versionCode in build.gradle.kts

## Notes

- The `beta` job only runs on the main branch
- The `release` job only runs when a git tag is pushed
- The `build` job runs on both main branch and tags
- All jobs require the secure files to be properly configured
