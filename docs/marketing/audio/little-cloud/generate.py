"""Original short, whimsical piano/celesta miniature; no existing melody or samples."""
import math, struct, wave
from pathlib import Path
OUTPUT = Path(__file__).resolve().parent
rate=24000
beat=60/112
length=12.5
left=[0.0]*int(rate*length)
right=[0.0]*len(left)

def note(midi,start,duration,velocity=0.2,pan=0.0,tone='piano'):
    frequency=440*2**((midi-69)/12)
    first=int(start*rate)
    n=min(int((duration+0.7)*rate),len(left)-first)
    for i in range(n):
        t=i/rate
        attack=1-math.exp(-t*350)
        release=1 if t<duration else math.exp(-(t-duration)*12)
        if tone=='piano':
            sound=sum(a*math.sin(2*math.pi*frequency*h*(1+0.00012*h*h)*t)*math.exp(-t*(1.4+h*0.5)) for h,a in ((1,1),(2,0.28),(3,0.14),(4,0.07)))
        else:
            sound=math.sin(2*math.pi*frequency*t)*math.exp(-t*2.8)+0.22*math.sin(2*math.pi*frequency*2.01*t)*math.exp(-t*5)
        value=velocity*attack*release*sound
        left[first+i]+=value*(1-pan)*0.5
        right[first+i]+=value*(1+pan)*0.5

# Bright added-note harmony, a lilting original motif and soft arpeggios.
chords=[(48,55,60,64,67),(45,52,57,60,64),(41,48,53,57,64),(43,50,55,59,62)]
for bar,chord in enumerate(chords):
    for step in range(8):
        pitch=chord[(0,2,3,4,1,2,3,4)[step]]
        note(pitch,bar*4*beat+step*beat/2,0.75*beat,0.15,pan=-0.24)
melody=[(76,0,0.75),(79,1,0.5),(83,1.5,0.5),(81,2,1.5),
        (79,4,0.5),(76,4.5,0.5),(74,5,0.75),(72,6,1.5),
        (74,8,0.5),(76,8.5,0.5),(79,9,1),(81,10,0.75),(83,11,0.5),
        (79,12,0.5),(74,12.5,0.5),(76,13,0.75),(72,14,2)]
for pitch,onset,duration in melody:
    note(pitch,0.05+onset*beat,duration*beat,0.21,pan=0.18,tone='celesta')
for pitch in (48,55,60,64,74): note(pitch,16.2*beat,1.7,0.11,pan=(pitch-60)/45)
# Gentle stereo reflections and final fade, with peak normalization.
for channel in (left,right):
    dry=channel[:]
    for delay,gain in ((0.11,0.11),(0.23,0.08),(0.39,0.05)):
        shift=int(delay*rate)
        for i in range(shift,len(channel)): channel[i]+=dry[i-shift]*gain
peak=max(max(map(abs,left)),max(map(abs,right)),0.01)
frames=bytearray()
for i,(l,r) in enumerate(zip(left,right)):
    fade=min(1.0,(len(left)-i)/(rate*1.1))
    frames+=struct.pack('<hh',int(l/peak*26000*fade),int(r/peak*26000*fade))
with wave.open(str(OUTPUT / 'little-cloud.wav'),'wb') as wav:
    wav.setnchannels(2); wav.setsampwidth(2); wav.setframerate(rate); wav.writeframes(frames)
print('Original 12.5-second piano/celesta miniature created.')
