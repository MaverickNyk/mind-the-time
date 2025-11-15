# MindTheTime - TFL Departure Board App

This is an Android application that provides real-time departure information for various Transport for London (TFL) services, designed to replicate the look and feel of the iconic retro departure boards.

## Features

- **Multi-Mode Selection**: Choose from a variety of transport modes, including Tube, DLR, Overground, and the Elizabeth Line.
- **Cascading Dropdowns**: A step-by-step selection process allows users to drill down from transport mode, to a specific station, line, and direction.
- **Searchable Menus**: All dropdowns are searchable for quick and easy selection.
- **Live Departure Board**: After a selection is made, the app displays a live-polling departure board that refreshes every 30 seconds with the next upcoming arrivals.
- **Authentic UI/UX**: The departure board is styled to mimic the classic TFL aesthetic, featuring:
    - A custom pixel/dot-matrix style font.
    - An amber-on-black color scheme with a subtle "glow" effect.
    - A tiling pixel-grid background to simulate a real LED screen.
    - A live-updating real-time clock.
- **Background Caching**: Station and line data is intelligently cached in the background using `WorkManager`, ensuring fast load times and a smooth user experience.

## Architecture

The app is built using modern Android development practices.

- **UI Layer**: The main user interface is managed by `SelectionActivity`, which handles user input and orchestrates the data flow. The complex departure board UI is encapsulated in a dedicated `DepartureBoard.kt` class for better separation of concerns.
- **Data Layer**: A `TflRepository` acts as the single source of truth for all data. It is responsible for:
    - Fetching data from the TFL API.
    - Managing the local file-based cache for station data.
- **Networking**: API requests to the TFL Unified API are handled using **Retrofit**.
- **Data Models**: Clear and concise Kotlin data classes (`StopPoint`, `Prediction`, `TransportMode`, etc.) are used to model the API responses and application state.
- **JSON Parsing**: The app uses **Gson** with a custom `StopPointDeserializer` to robustly parse complex and sometimes inconsistent API responses, cleaning data at the source.
- **Background Processing**: A `CacheAllStationsWorker`, managed by **WorkManager**, runs in the background to pre-fetch and cache station data, ensuring the app is responsive even on first use.

## Setup

To build and run the project, you will need to add a custom font file:

1.  Obtain a dot-matrix or pixel-style `.ttf` font file.
2.  Place it in the `app/src/main/res/font/` directory.
3.  Ensure the font is named according to the reference in the font XML file (e.g., `tfl_pixel_font.ttf`).
