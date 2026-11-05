# Privacy Policy for Share Sanitizer

**Effective Date:** November 5, 2026

**Share Sanitizer** (`com.sharesanitizer.app`) is a free, open-source privacy utility built with a strict **zero-trust, offline-first** architecture.

## 1. Zero Data Collection
Share Sanitizer **does not collect, store, transmit, or share any personal data, usage analytics, telemetry, crash reports, or device identifiers**.

## 2. Zero Permissions & 100% On-Device Processing
- **No Network Access:** The app does not request the `android.permission.INTERNET` permission. It is technically incapable of making network requests or transmitting your data to any server.
- **Text & URL Sanitization:** When you share or paste text/URLs into Share Sanitizer, all tracking parameter removal, redirect unwrapping, and invisible Unicode character stripping happen locally in your device's memory.
- **Image Metadata (EXIF) Stripping:** When you select or share images into Share Sanitizer, raw pixel data is decoded and re-encoded into a temporary local file in the app's private cache directory (`cacheDir/shared_images`) without embedded EXIF metadata (such as GPS coordinates, camera model, or timestamps). By default, this temporary cache is automatically cleared when the app starts.
- **User Preferences:** Settings (such as theme selection, custom tracking parameters, and export format) are stored strictly locally on your device using Android Jetpack DataStore.

## 3. Third-Party Services
Share Sanitizer contains **no third-party analytics SDKs, no advertising SDKs, and no tracking libraries**.

## 4. Open Source Verification
Share Sanitizer is licensed under the **GNU General Public License v3.0 (GPL-3.0)**. Anyone can inspect the full source code and verify these privacy guarantees at:
https://github.com/TheOwlKun/Share-Sanitizer

## 5. Contact
If you have any questions or concerns regarding this Privacy Policy, please open an issue on the GitHub repository:
https://github.com/TheOwlKun/Share-Sanitizer/issues
