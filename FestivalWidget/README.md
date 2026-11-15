# Festival Calendar Widgets

Eight bold home-screen widget styles. Each shows the time, weekday, month, day, and the next upcoming festival with a countdown. Written for low-end phones (Android 5.0+, no permissions, no libraries).

## Styles
| Widget | Size | Look |
|---|---|---|
| Neon Night | 4x2 | Black with neon-green outline |
| Retro Sunset | 4x2 | Red-to-orange gradient |
| Clean White | 3x2 | Black on white, minimal |
| Pink Pop | 3x2 | Y2K pink/purple gradient |
| Royal Gold | 4x1 | Slim black strip with gold text |
| Saffron | 4x2 | Traditional saffron and brown |
| Ocean Splash | 4x2 | Blue gradient, huge date |
| Giant Countdown | 3x2 | Neo-brutalist yellow, giant days-left number |

All widgets are resizable.

## Build
1. Open this folder in Android Studio (Koala or newer) and let Gradle sync.
2. Run on a device, or use Build > Build APK(s).
3. Release APK: `./gradlew assembleRelease` (shrunk with R8; signed with the debug key, so replace it before publishing).
4. Long-press the home screen > Widgets > "Festival Calendar".

## Why it is light on RAM and battery
- Zero dependencies (no AppCompat, Material, Compose or Glance). Widgets are plain `RemoteViews`, rendered by the launcher.
- Time, weekday, month and day are `TextClock` views. The system updates them itself, so the app does nothing each minute and handles timezone and 12/24h changes automatically.
- The app only pushes the festival name and countdown, once a day, using one inexact non-waking alarm.
- It re-calibrates when the date, time, timezone or language changes, after an app update, and when the app is opened.
- No services, no foreground notification, no internet, no permissions.
- Vector icon and shape drawables only; no bitmaps.

## Editing festivals
Edit `app/src/main/res/raw/festivals.txt`:
- `MM-DD,Name` repeats every year (fixed-date days).
- `YYYY-MM-DD,Name` is a one-off date (lunar festivals).

The list ships with fixed-date days plus lunar festivals from Nov 2026 to Dec 2027. Lunar dates can differ by a day between regions and Eid depends on moon sighting, so check them against your local panchang. Add 2028+ dates when needed.
