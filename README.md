# Pointfix samples

Small apps for trying [Pointfix](https://pointfix.dev): point at a UI problem in a running app, describe the fix, and let your local coding agent make the change.

There is one shop-style screen per platform. Each one has a **single intentional UI flaw** for you to practise on:

| Sample | Stack | The flaw |
| --- | --- | --- |
| [`ios/`](ios) | SwiftUI, iOS 26 | Product names in the grid use `lineLimit(1)`, so "Oversized Double-Breasted Wool Overcoat" is truncated to "Oversized Double-Breasted…" instead of wrapping onto a second line. |
| [`android/`](android) | Jetpack Compose | Product names use `maxLines = 1`, `softWrap = false` and `TextOverflow.Clip`, so long names are cut off mid-word with no ellipsis and no second line. |
| [`web/`](web) | Plain HTML/CSS + Vite | Product names use `white-space: nowrap` with no ellipsis, so "Oversized Corduroy Trucker Jacket" runs past the right edge of its card. |

Each sample uses the **published** Pointfix packages, exactly as your own app would.

| Platform | Package |
| --- | --- |
| iOS | Swift package `https://github.com/pointfix-dev/pointfix-ios` (from 0.2.0), product `PointfixKit` |
| Android | `dev.pointfix:pointfix-android:0.2.0` (debug) and `dev.pointfix:pointfix-android-noop:0.2.0` (release), from Maven Central |
| Web | `@pointfix/web` from npm, loaded only in development |

## Prerequisites

- The Pointfix Mac app: [pointfix.dev/download](https://pointfix.dev/download) or `brew install --cask pointfix-dev/tap/pointfix`
- The Pointfix CLI and bridge: `brew install pointfix-dev/tap/pointfix`
- A signed-in coding agent CLI that Pointfix supports (see [Agents](https://pointfix.dev/docs/agents))
- iOS: Xcode 27 or later (the sample targets iOS 26)
- Android: JDK 17 or later and Android SDK 35 (Android Studio provides both)
- Web: Node 20 or later

## Start the bridge

From the repository root, run:

```sh
pointfix start
```

`pointfix.config.json` at the root sets the project to the whole repository (`"project": "."`). That lets the agent edit any of the three samples. It also uses the default port 4747 and lists the web dev server (`http://localhost:5173` and `http://127.0.0.1:5173`) in `allowedOrigins`. Run `pointfix doctor` to check your setup. Reports and agent output are written to `.pointfix/`, which is git-ignored.

To have Pointfix rebuild and relaunch the app after a fix, turn on **Auto-relaunch** in the dashboard's Settings. It finds `ios/PointfixSample.xcodeproj` (scheme `PointfixSample`) and the Android app (`:app:installDebug`, `dev.pointfix.sample`) by itself. Web pages reload on their own.

## iOS quick start

```sh
open ios/PointfixSample.xcodeproj
```

Run the `PointfixSample` scheme on an iOS 26 simulator. Xcode fetches `PointfixKit` from `https://github.com/pointfix-dev/pointfix-ios`. Long-press a product name in the grid, describe the fix, and send it.

The Xcode project is generated from `ios/project.yml` with [XcodeGen](https://github.com/yonaskolb/XcodeGen). It is committed so the sample opens without XcodeGen. If you change `project.yml`, regenerate the project with `cd ios && xcodegen generate`.

From the command line:

```sh
cd ios
xcodebuild -scheme PointfixSample -destination 'platform=iOS Simulator,name=iPhone 17 Pro' build test
```

`PointfixSampleTests` checks the catalog data. `PointfixSampleUITests` long-presses a control and checks that the Pointfix composer opens.

## Android quick start

Open the `android/` folder in Android Studio and run the `app` configuration on an emulator or device. Or, from the command line:

```sh
cd android
./gradlew :app:installDebug
adb reverse tcp:4747 tcp:4747
```

`adb reverse` lets the app reach the bridge on your Mac. Run it again after you reconnect a device. Long-press a product name, describe the fix, and send it.

Debug builds use `pointfix-android`. Release builds (`./gradlew :app:assembleRelease`) use `pointfix-android-noop`, which has the same API and contains no capture code. `./gradlew :app:testDebugUnitTest` renders the shop screen in light and dark mode to `app/build/reports/sample/`.

## Web quick start

```sh
cd web
npm install
npm run dev
```

Open [http://localhost:5173](http://localhost:5173). Click **Report UI issue** at the bottom right, or Alt + Shift + click anything on the page. `main.js` loads `@pointfix/web` only when `import.meta.env.DEV` is true, so `npm run build` output contains no Pointfix code. The dev server is pinned to port 5173 (`strictPort`) to match `allowedOrigins`.

## Learn more

Read the full documentation at [pointfix.dev/docs](https://pointfix.dev/docs/), including the [iOS](https://pointfix.dev/docs/ios), [Android](https://pointfix.dev/docs/android) and [web](https://pointfix.dev/docs/web) guides.

## License

See [LICENSE.md](LICENSE.md).
