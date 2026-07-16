# CafeX

CafeX is a complete single-activity Android application built with Kotlin, Jetpack Compose, Material 3, Navigation Compose, MVVM, Firebase Authentication, and Firebase Realtime Database.

## Included features

- Animated splash screen with CafeX logo
- Full-screen original coffee background on authentication screens
- Email/password login, registration, password reset, validation, errors, and loading states
- Password visibility toggles
- Home menu with search and category filters
- Create, read, update, and delete menu items
- Item detail and profile screens
- Dark theme support
- Firebase repository implementations and secure starter Realtime Database rules
- Automatic demo mode when personal Firebase credentials are not present
- Unit tests for validation

## Open and run

1. Open this `CafeX` folder in the latest stable Android Studio.
2. Let Gradle sync.
3. Select an emulator or Android device running API 24 or newer.
4. Run the `app` configuration.

Without Firebase configuration, CafeX intentionally runs in demo mode. Register with any valid email, or sign in with any valid email and a password containing at least six characters. Menu CRUD works in memory so every screen can be tested immediately.

## Connect Firebase

1. Create a Firebase project at <https://console.firebase.google.com/>.
2. Add an Android app with package name `com.example.cafex`.
3. Download `google-services.json` and place it at `app/google-services.json`.
4. In Firebase Authentication, enable **Email/Password**.
5. Create a Firebase Realtime Database.
6. From the `firebase` directory, deploy the included rules with the Firebase CLI, or paste `firebase/database.rules.json` into the Realtime Database Rules editor.
7. Sync Gradle and rebuild the app.

The app build applies the Google Services plugin only when `app/google-services.json` exists. This keeps the project buildable before private project configuration is supplied and switches the repositories to Firebase automatically afterward.

## Firebase data shape

```text
users/{uid}
  id
  fullName
  email
  createdAt

items/{itemId}
  id
  name
  description
  price
  categoryId
  available
  createdBy
  createdAt
```

The provided rules allow each user to read/write their own profile. Authenticated users can read menu items, while only an item's creator can create, update, or delete it.

## Project structure

```text
app/src/main/java/com/example/cafex
├── di
├── model
├── navigation
├── repository
├── ui
│   ├── components
│   ├── screens
│   └── theme
├── utils
└── viewmodel
```

## Build versions

- Android Gradle Plugin 9.2.1
- Gradle 9.4.1
- compileSdk / targetSdk 37
- Compose BOM 2026.06.00
- Navigation Compose 2.9.8
- Lifecycle 2.11.0
- Firebase BoM 34.15.0

Firebase uses the current main modules (`firebase-auth` and `firebase-database`), not the retired Firebase KTX artifacts.

## Original visual asset

`app/src/main/res/drawable-nodpi/cafe_background.png` was generated specifically for CafeX with the built-in image generation workflow. Prompt summary: a photorealistic portrait specialty-coffee scene with a latte on dark walnut, warm amber light, a softly blurred café, and generous dark negative space for mobile authentication UI; no text, people, logos, or watermarks.
