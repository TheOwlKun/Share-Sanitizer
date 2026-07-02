# Share Sanitizer

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![F-Droid](https://img.shields.io/badge/F--Droid-available-green.svg)](https://f-droid.org)

A privacy-first Android app that sanitizes shared text and images to remove tracking metadata.

## Features
- **Text Sanitization**: Share a link or block of text with the app. It will detect URLs, remove common tracking parameters (like `utm_source`, `gclid`), and let you share the clean version.
- **Image Sanitization**: Share one or multiple images with the app. It will decode the pixel data and save a fresh copy, completely removing embedded EXIF metadata (GPS, date, camera info) to protect your privacy before you upload or share it elsewhere.
- **Fully Offline**: Zero network permissions. No analytics. No tracking. Nothing is uploaded anywhere.

## Privacy & Security
This application is designed with extreme privacy in mind. It requires **zero permissions** to operate effectively on modern Android devices. 
- It does not have the `INTERNET` permission, guaranteeing no data can leave your device.
- It processes all URLs and image pixel data locally using standard Android APIs.
- Built-in F-Droid installer warning to encourage reproducible, verifiable builds.

## How to Test Intent Flows
1. **Text**: Open your browser, select "Share" on a webpage, and choose "Share Sanitizer". The app will intercept the text, show a diff of the removed URL trackers, and provide a button to copy or share the clean version.
2. **Image**: Open your gallery, select a photo, tap share, and select "Share Sanitizer". The app will strip metadata and present the clean version for saving or further sharing.

## Build Instructions
This project uses Gradle.
- **Debug build**: `./gradlew assembleDebug`
- **Release build**: Create a signed key and configure `signingConfigs` in `app/build.gradle.kts`. Then run `./gradlew assembleRelease`.

## Contributing
Contributions are welcome! Please ensure any new features align with the "zero permissions, fully offline" ethos of the project.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
