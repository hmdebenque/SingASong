# Generated reader/audio fixtures

Run from the repository root:

```sh
python3 app/scripts/generate_fixtures.py --check
python3 app/scripts/generate_fixtures.py
```

The generator creates original synthetic material without downloading scores, melodies or recordings. Python's standard library reproduces PDF/WAV bytes deterministically. Regeneration additionally requires `ffmpeg` with `libmp3lame` for the MP3 variant; MP3 bytes may vary between encoder versions. The committed SHA-256 manifest verifies every asset, including MP3, without requiring ffmpeg during `--check`. Generated assets are test material, not a representative choir corpus or approved store artwork.

| Asset (under app/) | Purpose |
| --- | --- |
| `src/main/assets/demo/score.pdf` | Three vector pages with voice-labelled staves and schematic note marks; selectable generated demo |
| `src/main/assets/demo/practice.wav` | 32 seconds, mono 24 kHz, 16-bit PCM; original sine/harmonic pitch sequence with fades; selectable demo |
| `src/androidTest/assets/scan-like.pdf` | Original raster staves/noise embedded in a PDF, one page |
| `src/androidTest/assets/oversized.pdf` | 20000×28000-point vector page to test bounded rendering |
| `src/androidTest/assets/practice.mp3` | Same generated signal, encoded at 64 kbit/s; tests container decoding |
| `src/androidTest/assets/corrupt.pdf`, `corrupt.wav` | Deliberately invalid headers/bodies for rollback and error paths |

The generated drawing and tone sequence are independent fixtures. They do not demonstrate notation playback, lyric extraction, aligned stems or musical synchronization. Oversize streams and interrupted providers are generated in instrumentation tests instead of committing large files. The UI imports demo material through the same copy/validation/commit path as document-picker files; demo labels explicitly say generated.

No user/choir media is included. Synthetic fixture generation does not complete SAS-003's real-content, rights, workflow or pilot-device validation.
