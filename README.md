# TODO

- [x] create git repo
- [x] Record Audio
- [x] Save audio
- [x] Play Audio
- [x] Read file from external storage
- [x] Run openSMILE
- [x] Create mfcc
- [ ] Try to make openSMILE mfcc like librosa mfcc
- [ ] Create mfcc with 88 chars
- [ ] Read CSV
- [ ] Load model
- [ ] Classify mfcc
- [ ] Show label in GUI
- [ ] Load, classify and save results.csv of example audio on start option
- [ ] Evaluate dialog
- [ ] share evaluation / send via mail

# What is working

- 40mffc via openSMILE --> require 40mfcc model ideally trained by openSMILE (we have 40mfcc model)
- Rec Wav Audio

Next Steps
- write final Paper
- create Presentation
- see TODO

# Generate openSMILE config

openSMILE configs can be generated via SMILExtract.
See here for available options and pre configurations:
- https://audeering.github.io/opensmile/reference.html#components
- https://audeering.github.io/opensmile/get-started.html?highlight=android#extracting-features-for-emotion-recognition

    //Example to generate config for mfcc
    SMILExtract -cfgFileTemplate -configDflt cWaveSource,cFramer,cWindower,cTransformFFT,cFFTmagphase,cMelspec,cMfcc,cDeltaRegression,cDeltaRegression,cCsvSink -l 1 2> mfcc.config

# Steps to build openSMILE.aar

tested with archlinux

## setup system

install swig, java8 see here: https://github.com/audeering/opensmile/issues/36
```bash
pacman -Syu extra/swig extra/jre8-openjdk
```

set java8 as default java
```bash
archlinux-java set java-8-openjdk
```

## Clone Repo

```bash
git clone https://github.com/audeering/opensmile.git
cd opensmile/progsrc/android-template
```

## Adapt Code

change lines 71-73 in opensmile/src/main/java/com/audeering/opensmile/OpenSMILEJNI.java to:
```bash
public static boolean swigDirector_CallbackExternalSink_onCalledExternalSinkCallback(CallbackExternalSink jself, float[] data) {
  return jself.onCalledExternalSinkCallback(data);
}
```

change line 16 in opensmile/progsrc/android-template/opensmile/build.gradle to:
```bash
def path = 'opensmile/src/main/java/com/audeering/opensmile/OpenSMILEJNI.java'
```

set build args in opensmile/progsrc/android-template/gradle.properties to:
```bash
    CMAKE_FLAGS=-DWITH_OPENSLES=ON
    WITH_OPENSLES=ON
```

## Building lib (see README.md in opensmile/progsrc/android-template)

First, in order to build the Java SWIG files, install swig first, and then run:
```bash
./gradlew opensmile:swig
```
This has been tested with SWIG Version 4.0.1.

To build the aar libraries, set the openSMILE parameters in opensmile/build.gradle, then run:
```bash
./gradlew opensmile:assemble
```

## Copy Lib to Project

Copy the files `opensmile-debug.aar` and `opensmile-release.aar` to your project

