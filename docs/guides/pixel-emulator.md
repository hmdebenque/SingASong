# Pixel reference emulator

The maintainer requested a flagship Pixel from roughly four years before October 2026. The chosen reference is **Pixel 7 Pro**, released in October 2022 ([Google announcement](https://blog.google/products-and-platforms/devices/pixel/pixel-7-pixel-7-pro/)). This is an emulator/layout reference, not evidence of physical Tensor hardware performance, battery life or speaker/headphone behavior.

Use the official `pixel_7_pro` hardware profile with the already installed Android 16/API 36 default x86_64 image. API 36 matches the current application target and existing test coverage; it is not a claim to reproduce the Pixel's original shipping Android version or exact factory software. The current app does not require Google Play services, so no Google account or Play Store image is needed.

## Create once

Run from the repository root. The image and profile were already available on this machine; no additional download or license acceptance was necessary for this setup.

```sh
mise exec -- avdmanager create avd \
  --name singasong-pixel7pro-api36 \
  --package 'system-images;android-36;default;x86_64' \
  --device pixel_7_pro
```

On a different machine, install the emulator and that system image with `sdkmanager` first as described in the [Android guide](../../app/README.md). Do not force-overwrite an existing AVD. AVD files remain outside the repository.

## Launch and test

For a visible emulator, run in a dedicated terminal:

```sh
mise exec -- sh -c 'exec "$ANDROID_HOME/emulator/emulator" -avd singasong-pixel7pro-api36 -no-snapshot -gpu swiftshader -port 5560'
```

For automated checks, add `-no-window -no-audio -no-boot-anim -memory 2048 -cores 2`. The emulator may raise guest RAM to meet its image requirements. Port 5560 identifies this instance as `emulator-5560`; choose another free even port if occupied and adjust subsequent commands. Wait until `sys.boot_completed` reports `1` before installation/testing.

```sh
mise exec -- adb -s emulator-5560 shell getprop sys.boot_completed
mise exec -- adb -s emulator-5560 install -r app/build/outputs/apk/debug/SingASong-debug.apk
mise exec -- adb -s emulator-5560 shell am start -W -n com.frenchfrie.singasong/.MainActivity
ANDROID_SERIAL=emulator-5560 mise run test:app
```

The instrumentation suite uses generated content and may replace the test app's local workspace. Keep private pilot files separately and do not run destructive test fixtures against a valuable personal installation. The connected-test runner may uninstall the app after completing. Re-run the APK install and launch commands after testing, then use **Try generated demo** for a manual look, if needed.

Stop only this emulator with:

```sh
mise exec -- adb -s emulator-5560 emu kill
```

Pixel 2/API 26 and Pixel 6/API 36 AVDs from prior work remain available. Pixel 7 Pro does not replace minimum-SDK coverage or the later tablet/real-material pilot.

## Verified setup — 2026-10-31

Created the named AVD without changing existing AVDs. It booted with Android 16/API 36, x86_64, 1440×3120 and density 560. The runtime raised requested RAM to 4096 MB. APK installation and cold launch succeeded. All nine instrumentation tests passed with zero failures, errors or skips; [command evidence](../project/evidence/sas-003/pixel7pro-tests.txt) is retained. Reinstalled and launched the APK after test-runner cleanup, then stopped the headless emulator; the AVD and installed app remain ready for a visible launch.

This is generated-fixture emulator evidence only. Real scores/recordings and physical-device feedback remain open in SAS-003.
