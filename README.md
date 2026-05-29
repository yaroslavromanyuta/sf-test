# Stark Future — Telemetry Dashboard

Android technical assessment: a single-screen Jetpack Compose app that reads a static JSON telemetry snapshot for the Stark VARG MX 1.2 electric motocross bike and displays it as an interactive dashboard.

## Build & Run

```bash
./gradlew assembleDebug          # build APK
./gradlew :app:testDebugUnitTest # run unit tests
./gradlew :app:installDebug      # install on connected device/emulator
```

Minimum SDK: 26 · Target/Compile SDK: 36 · Java 17 · Kotlin 2.0.21

## Architecture

MVVM + Clean Architecture with strict layer separation:

```
UI (Compose)
  └── TelemetryDashboardRoute (ViewModel observation + state routing)
  └── TelemetryDashboardScreen (stateless rendering)
       └── TelemetryDashboardViewModel (StateFlow, HiltViewModel)
            └── GetTelemetrySnapshotUseCase
                 └── TelemetryRepository
                      └── AssetTelemetrySnapshotDataSource (reads asset)
                      └── KotlinxTelemetryJsonParser (deserialization)
                      └── TelemetryDtoToDomainMapper (DTO → domain)
```

**Key layers:**

| Layer | Package | Responsibility |
|---|---|---|
| Domain | `domain.model`, `domain.logic`, `domain.usecase` | Pure Kotlin models, business rules, use case interfaces |
| Data | `data.*` | Asset reading, JSON parsing, mapping to domain |
| Presentation | `presentation.dashboard` | UiState, UiModel, ViewModel, formatters |
| UI | `ui.dashboard`, `ui.components` | Compose screens and reusable components |

## Dependency Injection

Hilt with four modules:

- `CoreModule` — `Json` (kotlinx.serialization), `DispatcherProvider`
- `DataModule` — binds Repository, DataSource, Parser, Mapper
- `DomainModule` — binds UseCase, `BatteryStatusResolver`, `AverageSpeedCalculator`
- `PresentationModule` — binds `TelemetryUiMapper`, `DurationFormatter`, `TelemetryValueFormatter`

## UI & Design

Implements the Stark Future design system:

- **Dark-first** — dark theme is default; user can toggle Light/System via `ThemeSwitcher` in the top bar
- **Stark Red** `#E30613` as primary colour
- Battery section shows a progress bar colour-coded by status: green (≥50%), amber (16–49%), red (0–15%)
- Each telemetry section (`Battery`, `Performance`, `Ride Settings`, `Session`, `Warnings`) is independently expandable with animated visibility

## State Handling

`TelemetryDashboardUiState` is a sealed interface with four states:

- `Loading` — shown immediately on launch while the asset is read
- `Content(data)` — happy path; all telemetry rendered
- `Empty` — snapshot parsed but bike model is blank
- `Error(message)` — read or parse failure; includes a Retry button

## Testing

Unit tests cover all pure logic without Android framework dependencies:

| Test class | What it covers |
|---|---|
| `BatteryStatusResolverTest` | Boundary checks for Critical / Medium / Healthy / Unknown |
| `AverageSpeedCalculatorTest` | Normal case, zero/negative duration, zero distance |
| `DurationFormatterTest` | Minutes-only and hours+minutes formatting |
| `TelemetryUiMapperTest` | Full snapshot → UiModel mapping including formatters |
| `TelemetryDashboardViewModelTest` | State transitions, toggle, theme change, retry (MockK + Turbine) |

## Trade-offs & Notes

- **Static asset only** — no network layer; the JSON is bundled in `assets/telemetry_snapshot.json`
- **`AppResult`** wraps all repository responses so the ViewModel never catches exceptions directly
- **`DispatcherProvider`** is injected so coroutine dispatchers can be replaced in tests
- Coil handles bike image loading; a graceful fallback gradient is always rendered underneath