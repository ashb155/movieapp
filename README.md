# Flixist - Android with Jetpack Compose

## Project Summary

This is a modern Android application built using Kotlin and Jetpack Compose that allows users to browse and search for movies using The Movie Database (TMDb) API. The app features include:

- Displaying the latest movies with pagination support  
- Searching movies by title  
- Filtering movies by genres  
- Viewing detailed information about selected movies including cast and trailers  
- Smooth in-app navigation using Jetpack Navigation Compose  
- Embedded YouTube trailer player integration  
- Responsive UI built with Jetpack Compose and Material Design 3  

The app follows the MVVM architecture pattern and leverages Kotlin coroutines and StateFlow for asynchronous data handling and reactive UI updates.

## Technologies Used

- **Kotlin** — Primary programming language  
- **Jetpack Compose** — Modern declarative UI toolkit for Android  
- **AndroidX Libraries**  
  - Lifecycle (ViewModel, LiveData)  
  - Navigation Compose for in-app navigation  
- **Retrofit** — Type-safe HTTP client for REST API communication  
- **Gson Converter** — JSON serialization/deserialization for Retrofit  
- **Coil** — Image loading library optimized for Compose  
- **Google Accompanist** — Additional Compose utilities like navigation animations and swipe-to-refresh  
- **YouTube Android Player API** (via `androidyoutubeplayer`) — Embedded YouTube video playback  
- **Coroutines** — Asynchronous programming support with structured concurrency  
- **MVVM Architecture** — Clean separation of UI, business logic, and data handling  
- **StateFlow / MutableStateFlow** — Reactive state management with Kotlin flows  
