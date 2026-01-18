# ProductScannerApp (HTW Berlin)

A modern Android barcode scanner app that fetches product data from **Open Food Facts** and classifies products into **Halal / Vegetarian / Vegan** (always shown as 3 chips).  
Built with **Jetpack Compose**, **Navigation**, **ViewModels**, **Coroutines**, and supports **offline fallback** via **Room**.

---

## Features

### Functional requirements
- ✅ Scan barcodes using **Google Code Scanner (ML Kit)**
- ✅ Manual barcode input (bottom sheet)
- ✅ Product Detail screen:
  - product name / brand
  - hero image (if available)
  - quick facts (quantity, Nutri-Score, categories)
  - ingredients + explanation (“Why this result?”)
  - copy barcode + share product
- ✅ History screen:
  - shows recent scans
  - shows category chips
  - shows quick facts (quantity + Nutri-Score)
  - clear history button

### Non-functional requirements (from course)
- ✅ Jetpack Compose UI
- ✅ Up-button in AppBar (for non-top destinations)
- ✅ Navigation Drawer (fast navigation between screens)
- ✅ Navigation component + ViewModels + state handling
- ✅ Custom styling + icons (modern/glass look)
- ✅ Online access (OFF API) + **offline fallback**
- ✅ Async operations using **Kotlin Coroutines**
- ✅ Runs on Android **7+** up to at least Android **12+**

---

## Screens
- **Scanner**: open camera scanner or enter barcode manually
- **Detail**: product info + triad classification + reasons
- **History**: saved scans and chips
- **Settings**: placeholder / preferences
- **About**: app + data sources info

---

## Tech Stack
- **UI**: Jetpack Compose (Material 3)
- **Navigation**: Navigation Compose
- **Scanning**: Google ML Kit **Code Scanner API** (GmsBarcodeScanning)
- **Networking**: Retrofit + Moshi + OkHttp
- **Images**: Coil (AsyncImage)
- **Persistence**: Room (cache + history)
- **Async**: Kotlin Coroutines + Flow

---

## Data Source
Product data is fetched from:
- **Open Food Facts** (https://world.openfoodfacts.org/)

We request localized fields when possible:
- `?lc=de` for German-readable ingredients where available.

---

## Classification Logic (Triad)
The app always shows **3 chips**:
1. **Halal**: `HALAL` / `NON_HALAL` / `Halal: Unknown`
2. **Vegetarian**: `VEGETARIAN` / `NOT_VEGETARIAN` / `Vegetarian: Unknown`
3. **Vegan**: `VEGAN` / `NOT_VEGAN` / `Vegan: Unknown`

**Signals used:**
- Open Food Facts strong tags: `ingredients_analysis_tags`, `labels_tags`
- Ingredient keyword matching (DE/EN/FR) with word-boundaries to reduce false matches

⚠️ Note: This is a best-effort classification and not a certification.

---

## Project Structure (high-level)
- `ui/app/` → Navigation host + Drawer + AppRoute
- `ui/screens/scan/` → Scan UI + manual entry
- `ui/screens/detail/` → Product details + hero + collapsibles
- `ui/screens/history/` → History list + cards
- `data/remote/off/` → OFF Retrofit API + DTOs
- `data/repository/` → Repositories (network + Room fallback)
- `data/local/db/` → Room database, DAOs, entities
- `data/classification/` → ProductClassifier

---

## Setup & Run (Android Studio)
1. Open project in **Android Studio**
2. Sync Gradle
3. Run on:
   - Emulator, or
   - Real device (Android 7+ recommended)

### Device testing (real phone)
To test on a Nokia (Android 10):
1. Enable **Developer Options**
2. Enable **USB Debugging**
3. Connect via USB
4. Select device in Android Studio and press **Run**

---

## Build APK (for submission)
To generate a release APK:

**Android Studio**
1. `Build > Generate Signed Bundle / APK...`
2. Choose **APK**
3. Create/select a keystore
4. Select **release**
5. Finish

Output typically:
- `app/release/app-release.apk`

Upload the APK to the HTW cloud folder (as instructed by the course).

---

## Offline Behavior
- If network is available: app fetches from Open Food Facts and stores core fields to Room.
- If offline: app shows cached product data (if previously scanned).

---

## Known Limitations
- Open Food Facts entries can be incomplete.
- Halal status is only shown as **HALAL** if OFF provides a halal label tag; otherwise it may be unknown.
- Keyword matching is heuristic and may have false positives/negatives.

---

## License / Attribution
- Product data: Open Food Facts contributors (ODbL)
- Icons: Material Icons
- Built for the HTW Berlin course project

---

## Authors
- Group: Team 3 (Zul & Franc)
- Course: Mobile-Anwendung im Ingenieurwisenschaften (WiSe 2025/2026)
