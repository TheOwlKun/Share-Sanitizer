<div align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="128" height="128" alt="Share Sanitizer Icon">
  
  <h1>Share Sanitizer</h1>
  
  <p><b>A zero-trust, completely offline Android privacy tool to strip tracking metadata from your shared URLs and Images.</b></p>

  [![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
  [![F-Droid](https://img.shields.io/badge/F--Droid-Submission_Pending-orange.svg)](https://gitlab.com/fdroid/rfp/-/issues)
</div>

<br>
<div align="center">
  <img src="fastlane/metadata/android/en-US/images/featureGraphic.png" alt="Feature Graphic">
</div>

<br>
<div align="center">
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg" width="30%" alt="Main Screen">
  &nbsp;
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg" width="30%" alt="Settings Screen">
  &nbsp;
  <img src="fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg" width="30%" alt="About Screen">
</div>

---

## 🛡️ Core Features

* **URL Tracking Removal**: Seamlessly intercept shared links (or text containing links) and automatically strip out sneaky marketing and analytics parameters (e.g. `utm_source`, `gclid`, `fbclid`). Share the clean link instantly.
* **Image Metadata Stripping**: When you share a photo to the app, it safely decodes the raw pixel data and strips all embedded EXIF metadata (GPS location, camera make/model, timestamps) before you upload it to social media or messaging platforms.
* **Zero Trust & Fully Offline**: Share Sanitizer operates entirely on-device. It requires **zero** permissions (not even `INTERNET`). Nothing is ever uploaded, and no analytics are collected.
* **Material You Design**: A beautiful, modern interface supporting Android 12+ dynamic theming, dark mode, and fluid micro-animations.

## 📱 How it Works

1. **Text**: Highlight text or click "Share" on a webpage in your browser, and select **Share Sanitizer**. The app intercepts it, highlights the removed trackers, and lets you immediately share the clean version.
2. **Images**: Select one or multiple photos from your Gallery, tap share, and choose **Share Sanitizer**. It strips the metadata and hands the clean images back to your sharing menu.

## 🛠️ Build Instructions

This project uses modern Android development standards (Kotlin, Jetpack Compose, Material 3) and builds via Gradle.

```bash
# Clone the repository
git clone https://github.com/TheOwlKun/Share-Sanitizer.git

# Enter the directory
cd Share-Sanitizer

# Build the debug APK
./gradlew assembleDebug
```

## 🤝 Contributing

Contributions are incredibly welcome! Please ensure any new features strictly align with the "zero permissions, fully offline" ethos of the project.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License
This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
