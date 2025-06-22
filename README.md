Server - https://github.com/goyalvishrut/MediMitr-server
# MediMitra: Your Online Medicine Ordering App

---

## 🚀 Project Overview

MediMitra is a comprehensive online medicine ordering platform, consisting of a **cross-platform mobile application** and a **robust backend server**. Our goal is to simplify the process of ordering medicines, allowing users to browse products, manage prescriptions, and track orders seamlessly from their mobile devices.

---

## ✨ Features

### Mobile Application (Client)

The MediMitra client offers a rich, intuitive user experience with a **fully shared UI and business logic** across both Android and iOS, thanks to Kotlin Multiplatform Mobile (KMM).

* **User Authentication & Authorization:** Secure sign-up and login using email.
* **Product Catalog:** Browse medicines by categories.
* **Order Management:**
    * Place new orders.
    * View active orders.
    * Access past order history.
* **Promotions:** Discover and apply special offers and discounts.
* **Seamless Cross-Platform Experience:** Built with Kotlin Multiplatform Mobile (KMM) for a native look and feel on both Android and iOS from a single codebase.



## 🛠️ Technologies Used

* **Kotlin Multiplatform Mobile (KMM):** For shared business logic and UI across Android and iOS.
* **Kotlin:** Primary programming language.
* **Jetpack Compose:** (Likely used for Android UI)
* **SwiftUI:** (Likely used for iOS UI)

This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
