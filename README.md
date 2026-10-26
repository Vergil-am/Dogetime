# DogeTime 🐕

A Kotlin Multiplatform + Compose Multiplatform app for browsing movies and TV shows with [TMDB](https://www.themoviedb.org/), targeting **Android**, **iOS** and **Desktop** (Linux / macOS / Windows).

## Features

- **Home**: four rows: Popular Movies, Popular TV Shows, Trending Movies This Week, Trending TV Shows This Week.
- **Browse**: switch between Movies and TV Shows, then pick a subcategory (Popular, Top Rated, Now Playing / On The Air, Upcoming / Airing Today). Scroll to load more.
- **Search**: debounced multi-search across movies and TV shows.
- **Details**: backdrop, poster, rating, genres, runtime or seasons/episodes, tagline, overview and top cast.

## Architecture

MVVM with a unidirectional data flow, shared across all platforms:

```
ui/<screen>/   Screen (Compose) ──observes──▶ ViewModel ──calls──▶ MediaRepository ──▶ TmdbApi (Ktor)
                                  StateFlow                        maps DTOs → domain models
```

- `data/`: Ktor client, TMDB DTOs (kotlinx.serialization), repository that maps DTOs to domain models.
- `domain/`: `MediaItem`, `MediaDetails`, `BrowseCategory`, etc. Plain Kotlin, no framework types.
- `ui/<screen>/`: one ViewModel and one Screen per destination. ViewModels expose `StateFlow<UiState<T>>`.
- `core/`: a small multiplatform ViewModel base, `UiState`, and a platform back-handler `expect`/`actual`.
- `navigation/`: a `Navigator` back stack. Bottom-bar tabs are the root; details are pushed on top.
- `di/`: `AppContainer` builds the HTTP client and repository once.

## TMDB API key

The app reads `TMDB_API_KEY` from `secrets.properties` at the project root. That file is git-ignored.

```bash
cp secrets.properties.example secrets.properties   # then set TMDB_API_KEY
```

The key is compiled into the app, so only use a key you're comfortable shipping in a client build.

## Project layout

```
dogetime/
├── composeApp/                              # shared KMP module (Android library, iOS, desktop)
│   └── src/
│       ├── commonMain/kotlin/dev/dogetime/  # App, ui/, data/, domain/, core/, navigation/, di/
│       ├── iosMain/                         # MainViewController (exposed to Swift)
│       ├── androidMain/                     # Android platform actuals
│       └── desktopMain/                     # Desktop main() with Window
├── androidApp/                              # Android application module (MainActivity)
├── iosApp/iosApp/                           # SwiftUI host for the iOS framework
├── gradle/libs.versions.toml                # version catalog
└── settings.gradle.kts, build.gradle.kts
```

## Requirements

- JDK 17+ (JDK 21 works)
- Android SDK 37 (install via Android Studio's SDK Manager)
- Xcode, latest release (for iOS, macOS only)

Gradle does not need to be installed. The wrapper pins **Gradle 9.8.2** and downloads it on first run.

## Local setup

Point Gradle at your Android SDK (skip if you open the project in Android Studio, which does this for you):

```bash
echo "sdk.dir=$HOME/Android/Sdk" > local.properties   # macOS: ~/Library/Android/sdk
```

## Run

**Desktop**

```bash
./gradlew :composeApp:run
```

**Android**

```bash
./gradlew :androidApp:installDebug
```

…or pick the `androidApp` run configuration in Android Studio.

**iOS**

The iOS framework is built by Xcode. Create the Xcode project once:

1. In Xcode: *File → New → Project → iOS App* (SwiftUI, name `iosApp`), save it into `dogetime/iosApp/`.
2. Delete the generated `iOSApp.swift` / `ContentView.swift` and add the two files from `iosApp/iosApp/`.
3. Add a *Run Script* build phase **before** *Compile Sources* with:

   ```bash
   cd "$SRCROOT/.."
   ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
   ```

4. Under *Build Settings → Framework Search Paths* add `$(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)`.
5. Under *Build Settings → User Script Sandboxing* set it to **No**.
6. Add the `ComposeApp` framework to *Frameworks, Libraries, and Embedded Content* (embed & sign) once it has been built.

Then run on a simulator from Xcode.

## Package desktop installers

```bash
./gradlew :composeApp:packageDmg    # macOS
./gradlew :composeApp:packageMsi    # Windows
./gradlew :composeApp:packageDeb    # Linux
```

## Versions

| Component             | Version |
|-----------------------|---------|
| Gradle                | 9.8.2   |
| AGP                   | 9.4.1   |
| Kotlin                | 2.4.21  |
| Compose Multiplatform | 1.12.2  |
| Material3             | 1.9.0   |
| Ktor                  | 3.6.0   |
| Coil                  | 3.6.3   |
| kotlinx-serialization | 1.12.0  |
