# PlanetExplorer

Take-home assignment for an Android application that displays planets from the Star Wars API (SWAPI) with a clean, modern UI.

## Architecture Overview

This project follows **Clean Architecture** principles with clear separation of concerns:

- **UI Layer**: Jetpack Compose for list screen, Fragment for detail screen
- **Domain Layer**: Use cases and repository interfaces
- **Data Layer**: Repository implementation, API service, DTOs, and mappers

**Technology Stack:**

- Jetpack Compose & Material Design 3
- Kotlin Coroutines & Flow
- Dagger Hilt for dependency injection
- Retrofit + Kotlin Serialization for networking
- Coil for image loading
- Navigation Component with type-safe arguments

## Key Design Decisions & Trade-offs

### 1. **Hybrid UI Approach (Compose + Fragment)**
Used Jetpack Compose for the planet list screen and a traditional Fragment for the
detail screen.

- Ability to work with both modern (Compose) and traditional (Fragment) Android UI
  frameworks
- Fragment-Compose interoperability for gradual migration
- Detail screen uses View Binding with XML layouts

### 2. **Navigation Architecture**
Used Compose Navigation with Fragment interop instead of Fragment-based Navigation +
Compose views.

- Compose Navigation is the modern, recommended approach
- Type-safe navigation with Kotlin Serialization
- Compose is the host, Fragment is embedded
- **Ultimately, I decided to opt for this implementation because** when converting the detail Fragment to Compose later, we simply replace
  the `AndroidFragment` composable with the new Composable screen - no need to rewrite the entire
  navigation stack

### 3. **Passing Planet Data vs. Making API Call**
I opted to pass the entire Planet object through navigation instead of just ID and making a separate
API call.

**Why**:

- Avoids unnecessary network call for data we already have
- Better user experience (instant details screen)
- Reduces API load

**Trade-off**: Slightly longer navigation arguments (JSON-encoded Planet), but negligible for small
objects. However, not considered a recommended approach for cases in which the data is quickly changing.

### 4. **Result Wrapper Pattern**
Custom `Result<T>` sealed interface instead of Kotlin's built-in Result.

**Why**:
- More suitable for UI state management
- Clear separation between Loading/Success/Failure
- We are able to add our own custom errors to it if we want (DomainError, HttpError, NetworkError, etc)

**Trade-off**: Custom implementation to maintain, but provides exactly what we need.

## Project Structure

```
app/src/main/java/com/mystic/planetexplorer/
├── core/
│   ├── designsystem/       # Reusable UI components & theme
│   ├── model/              # Domain models
│   └── network/            # Result wrapper & dispatchers
├── data/
│   ├── api/                # Retrofit service
│   ├── dto/                # Data transfer objects
│   ├── mapper/             # DTO to domain model mappers
│   └── repository/         # Repository implementation
├── domain/
│   ├── repository/         # Repository interface
│   └── usecase/            # Business logic use cases
├── di/                     # Dependency injection modules
└── ui/
    ├── navigation/         # Navigation setup
    └── screens/            # UI screens (list & detail)
```

## Features

- Clean, maintainable code with documentation
- Proper error handling with retry functionality
- Loading states throughout the app
- Null safety for optional API fields
- Edge case handling (malformed data, network failures)
- Material Design 3 theming

## Setup

Before building the project, add the SWAPI base URL to your `local.properties` file:

```properties
SWAPI_BASE_URL=https://swapi.dev/api/
```

This file is git-ignored and should not be committed to version control.

## Building the Project

```bash
./gradlew assembleDebug
```
or
```bash
./gradlew assembleRelease
```

---
