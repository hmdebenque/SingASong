#!/usr/bin/env python3
"""Original deterministic PDF/WAV test material; optional MP3 encoding uses ffmpeg."""
import argparse
import hashlib
import io
import json
import math
from pathlib import Path
import shutil
import struct
import subprocess
import wave
import zlib

ROOT = Path(__file__).resolve().parents[1]


def pdf(pages, raster=False, oversized=False):
    objects = [b'', b'', b'<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>']
    kids = []
    for index in range(pages):
        page_id = len(objects) + 1
        stream_id = page_id + 1
        kids.append(f'{page_id} 0 R')
        width, height = (20000, 28000) if oversized else (600, 840)
        commands = ['1 1 1 rg 0 0 600 840 re f', '0 0 0 rg',
                    'BT /F1 24 Tf 40 790 Td (Quiet Rehearsal Study) Tj ET',
                    f'BT /F1 12 Tf 40 765 Td (Generated test score - page {index + 1}) Tj ET',
                    'BT /F1 10 Tf 40 745 Td (Original synthetic material. Not choir repertoire.) Tj ET']
        for voice, label in enumerate(('Soprano', 'Alto', 'Tenor', 'Bass')):
            y = 660 - voice * 130
            commands += [f'BT /F1 12 Tf 40 {y + 28} Td ({label}) Tj ET']
            for staff in range(5):
                commands += [f'0.5 w 40 {y - staff * 8} m 560 {y - staff * 8} l S']
            for note in range(12):
                x, ny = 70 + note * 40, y - 8 * ((note + voice + index) % 5)
                commands += [f'{x} {ny} 8 5 re f', f'1 w {x + 8} {ny} m {x + 8} {ny + 27} l S']
        commands += ['BT /F1 12 Tf 40 75 Td (Practice slowly. Page and audio positions are independent.) Tj ET']
        if oversized:
            commands = [f'q {width / 600} 0 0 {height / 840} 0 0 cm'] + commands + ['Q']
        if raster:
            # Original staff-like monochrome raster, with deterministic simulated scan noise.
            w, h = 600, 700
            pixels = bytearray([255]) * (w * h)
            for y in range(h):
                for x in range(w):
                    line = 30 < x < 570 and any(abs(y - (80 + v * 140 + s * 8)) <= 1 for v in range(4) for s in range(5))
                    note = any(((x - (65 + n * 42)) / 5)**2 + ((y - (95 + v * 140 + (n % 4) * 4)) / 4)**2 < 1 for v in range(4) for n in range(12))
                    if line or note:
                        pixels[y * w + x] = 20
                    elif (x * 131 + y * 177) % 193 == 0:
                        pixels[y * w + x] = 220
            image_id = stream_id + 1
            stream = f'q 520 0 0 620 40 80 cm /Scan Do Q\nBT /F1 16 Tf 40 780 Td (Generated scan-like score) Tj ET'.encode()
            resources = f'/Font << /F1 3 0 R >> /XObject << /Scan {image_id} 0 R >>'
        else:
            stream = '\n'.join(commands).encode()
            resources = '/Font << /F1 3 0 R >>'
        objects += [f'<< /Type /Page /Parent 2 0 R /MediaBox [0 0 {width} {height}] /Resources << {resources} >> /Contents {stream_id} 0 R >>'.encode(),
                    f'<< /Length {len(stream)} >>\nstream\n'.encode() + stream + b'\nendstream']
        if raster:
            compressed = zlib.compress(pixels, 9)
            objects += [f'<< /Type /XObject /Subtype /Image /Width {w} /Height {h} /ColorSpace /DeviceGray /BitsPerComponent 8 /Filter /FlateDecode /Length {len(compressed)} >>\nstream\n'.encode() + compressed + b'\nendstream']
    objects[0] = b'<< /Type /Catalog /Pages 2 0 R >>'
    objects[1] = f'<< /Type /Pages /Kids [{" ".join(kids)}] /Count {pages} >>'.encode()
    out = bytearray(b'%PDF-1.4\n%generated-fixture\n')
    offsets = [0]
    for i, obj in enumerate(objects, 1):
        offsets.append(len(out))
        out.extend(f'{i} 0 obj\n'.encode() + obj + b'\nendobj\n')
    xref = len(out)
    out.extend(f'xref\n0 {len(objects) + 1}\n0000000000 65535 f \n'.encode())
    out.extend(''.join(f'{n:010} 00000 n \n' for n in offsets[1:]).encode())
    out.extend(f'trailer\n<< /Size {len(objects) + 1} /Root 1 0 R >>\nstartxref\n{xref}\n%%EOF\n'.encode())
    return bytes(out)


def audio():
    sample_rate = 24000
    samples = bytearray()
    for i in range(sample_rate * 32):
        t = i / sample_rate
        note_time = t % 0.5
        frequency = (220, 261.625565, 293.664768, 329.627557, 349.228231, 329.627557, 293.664768, 261.625565)[int(t * 2) % 8]
        envelope = min(note_time * 35, 1) * max(0, min((0.5 - note_time) * 20, 1))
        value = int(6500 * envelope * (math.sin(2 * math.pi * frequency * t) + 0.2 * math.sin(4 * math.pi * frequency * t)))
        samples += struct.pack('<h', value)
    out = io.BytesIO()
    with wave.open(out, 'wb') as wav:
        wav.setnchannels(1)
        wav.setsampwidth(2)
        wav.setframerate(sample_rate)
        wav.writeframes(samples)
    return out.getvalue()


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--check', action='store_true', help='Compare generated PDF/WAV bytes with tracked fixtures')
    args = parser.parse_args()
    files = {'src/main/assets/demo/score.pdf': pdf(3), 'src/main/assets/demo/practice.wav': audio(),
             'src/androidTest/assets/scan-like.pdf': pdf(1, raster=True),
             'src/androidTest/assets/oversized.pdf': pdf(1, oversized=True),
             'src/androidTest/assets/corrupt.pdf': b'%PDF-1.4\nintentionally corrupt generated fixture\n',
             'src/androidTest/assets/corrupt.wav': b'RIFF\x00\x00\x00\x00WAVEcorrupt generated fixture'}
    for relative, content in files.items():
        path = ROOT / relative
        if args.check:
            if not path.exists() or path.read_bytes() != content:
                raise SystemExit(f'Fixture differs: {relative}')
        else:
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_bytes(content)
    mp3 = ROOT / 'src/androidTest/assets/practice.mp3'
    if not args.check:
        if not shutil.which('ffmpeg'):
            raise SystemExit('ffmpeg is required to regenerate the optional MP3 fixture')
        subprocess.run(['ffmpeg', '-hide_banner', '-loglevel', 'error', '-y', '-i', str(ROOT / 'src/main/assets/demo/practice.wav'), '-map_metadata', '-1', '-codec:a', 'libmp3lame', '-b:a', '64k', '-write_xing', '0', str(mp3)], check=True)
        files['src/androidTest/assets/practice.mp3'] = mp3.read_bytes()
        manifest = {name: hashlib.sha256(content).hexdigest() for name, content in files.items()}
        (ROOT / 'scripts/fixtures-sha256.json').write_text(json.dumps(manifest, indent=2) + '\n')
    manifest = json.loads((ROOT / 'scripts/fixtures-sha256.json').read_text())
    for relative, expected in manifest.items():
        assert hashlib.sha256((ROOT / relative).read_bytes()).hexdigest() == expected, relative
    print(f'{len(manifest)} original fixtures verified; PDF/WAV bytes reproduced deterministically.')


if __name__ == '__main__':
    main()
