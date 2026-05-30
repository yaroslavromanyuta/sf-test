# Stark Future — Telemetry Dashboard

Android technical assessment: a single-screen Jetpack Compose app that reads a static JSON telemetry snapshot for the Stark VARG MX 1.2 electric motocross bike and displays it as an interactive dashboard.

## Build & Run

```bash
./gradlew assembleDebug                    # build APK
./gradlew :app:testDebugUnitTest           # run unit tests
./gradlew connectedAndroidTest             # run instrumented UI tests (requires device/emulator)
./gradlew :app:recordPaparazziDebug        # record Paparazzi screenshot golden images
./gradlew :app:verifyPaparazziDebug        # verify screenshots against golden images
./gradlew :app:installDebug                # install on connected device/emulator
```

Minimum SDK: 24 · Target/Compile SDK: 35 · Java 17 · Kotlin 2.3.21 · AGP 9.2.1 · Compose BOM 2025.01.00 · Hilt 2.59.2

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
| Data | `data.*` | Repository interface + impl, asset reading, JSON parsing, mapping to domain |
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
- Warning banner is hidden when there are no active warnings

## State Handling

`TelemetryDashboardUiState` is a sealed interface with four states:

- `Loading` — shown immediately on launch while the asset is read
- `Content(data)` — happy path; all telemetry rendered
- `Empty` — snapshot parsed but bike model is blank
- `Error(message)` — read or parse failure; includes a Retry button

## Testing

### JVM Unit Tests

| Test class | What it covers |
|---|---|
| `BatteryStatusResolverTest` | Boundary checks for Critical / Medium / Healthy / Unknown |
| `AverageSpeedCalculatorTest` | Normal case, zero/negative duration, zero distance |
| `DurationFormatterTest` | Minutes-only and hours+minutes formatting |
| `TelemetryValueFormatterImplTest` | Speed, power, temperature, percentage, range formatting |
| `KotlinxTelemetryJsonParserTest` | Valid JSON, fault codes, warnings, unknown fields, invalid input |
| `TelemetryDtoToDomainMapperImplTest` | DTO → domain model mapping, severity and status resolution |
| `TelemetryRepositoryImplTest` | Success path, IOException/SerializationException error mapping, flow continuity |
| `GetTelemetrySnapshotUseCaseImplTest` | Delegation to repository, error and multi-emission propagation |
| `TelemetryUiMapperTest` | Full snapshot → UiModel mapping including formatters |
| `TelemetryDashboardViewModelTest` | State transitions, toggle, theme change, retry (MockK + Turbine) |

### JVM Integration Test

| Test class | What it covers |
|---|---|
| `TelemetryDataPipelineTest` | Parser + mapper end-to-end: field mapping, warnings, fault codes, empty diagnostics |

### Paparazzi Screenshot Tests (JVM)

| Test class | What it covers |
|---|---|
| `ComponentScreenshotTest` | `WarningBanner` (single, multiple, empty) and `BatteryProgressIndicator` (healthy, medium, critical) |
| `DashboardScreenshotTest` | All four screen states × dark/light theme; content variants (expanded, collapsed, critical battery) |

Golden images are committed to `app/src/test/snapshots/images/`.

### Instrumented UI Tests

| Test class | What it covers |
|---|---|
| `TelemetryLoadingStateTest` | Loading message is displayed |
| `TelemetryEmptyStateTest` | Empty state title and body text |
| `TelemetryErrorStateTest` | Error title, message, Retry button and callback |
| `TelemetryDashboardScreenTest` | Bike model, battery %, warning, expanded/collapsed sections, motor metrics, fault codes, max speed label, ride mode |

## Trade-offs & Notes

- **Local assets only** — no network layer; telemetry data is served from bundled JSON files in `assets/`. Three snapshots (`telemetry_snapshot.json`, `telemetry_snapshot_2.json`, `telemetry_snapshot_3.json`) are cycled every 15 seconds. This simulates future live-telemetry updates while staying within the static/local data constraint of the assessment.
- **Flow-based repository** — `TelemetryRepository.getTelemetrySnapshotFlow()` returns a `Flow` rather than a one-shot suspend function. This is a deliberate forward-looking choice: the same interface can be backed by WebSocket or BLE without changing the use case or ViewModel. For the assessment it simply emits JSON snapshot emissions on a timer.
- **Non-empty fault codes in `telemetry_snapshot.json`** — the main snapshot intentionally includes fault codes to exercise the diagnostics/warnings section of the UI. This is a deliberate extension beyond the minimal payload, not a misunderstanding of the task.
- **`AppResult`** wraps all repository responses so the ViewModel never catches exceptions directly
- **`AppError.EmptyData`** maps to `TelemetryDashboardUiState.Empty`, not `Error` — it signals a valid but empty payload, not a technical failure
- **`DispatcherProvider`** is injected so coroutine dispatchers can be replaced in tests
- Coil handles bike image loading; a graceful fallback gradient is always rendered underneath
