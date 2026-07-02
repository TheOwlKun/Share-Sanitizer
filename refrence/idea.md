# Share Sanitizer

A privacy-first Android application designed to clean tracking metadata from text and images before sharing.

## Core Features (Version 1)
1. **Strip Image Metadata**: Locally decode images and re-encode them to remove all EXIF metadata (GPS, camera info, timestamps).
2. **Normalize Text**: Identify URLs in text and remove tracking parameters (e.g., `utm_source`, `gclid`).
3. **Safe Share**: Provide a one-tap action to share the sanitized output.

All processing must occur strictly on-device with zero network permissions.