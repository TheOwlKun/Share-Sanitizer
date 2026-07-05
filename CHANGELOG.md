# Changelog

All notable changes to Share Sanitizer will be documented in this file.

## [1.1.0] - 2026-07-05

### Added
- Feature: Strip invisible unicode characters (zero-width spaces) used for fingerprinting.
- Feature: Unwrap tracking redirect URLs (Google, Facebook, YouTube, etc.) to get the real destination.
- Feature: Detect and show a warning for shortened URLs (bit.ly, t.co, etc.) since they can't be verified offline.
- Extracted all strings to `strings.xml` to support community translations.
- Added settings toggles for the new sanitization features.

### Changed
- Updated the installer warning dialog to mention IzzyOnDroid alongside F-Droid.
- Migrated away from deprecated `Divider` in Compose.

### Fixed
- Removed `printStackTrace()` calls to prevent leaking stack traces to logcat (improves privacy/F-Droid compliance).
- Fixed a minor nullable warning when importing filter lists.
- Code cleanup: removed unused imports and unnecessary comments.

## [1.0.1] - 2025-07-02

### Added
- Disabled dependency metadata embedding for F-Droid/IzzyOnDroid transparency compliance.
- Production signing configuration via `local.properties`.
- Gradle wrapper checksum verification.

### Changed
- Removed version text from HomeScreen UI.

## [1.0.0] - 2025-07-02

### Added
- Initial release.
- URL tracking parameter removal (500+ parameters + ClearURLs community rules).
- Image EXIF metadata stripping (JPEG, PNG, WebP).
- Custom tracking parameter management.
- AdGuard filter list import.
- Material You theming with dark mode support.
- Zero permissions, fully offline architecture.
